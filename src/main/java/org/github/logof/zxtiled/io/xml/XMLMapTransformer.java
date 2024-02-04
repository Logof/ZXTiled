/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package org.github.logof.zxtiled.io.xml;

import lombok.Setter;
import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.core.MapTypeEnum;
import org.github.logof.zxtiled.core.ObjectLayer;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.io.ImageHelper;
import org.github.logof.zxtiled.io.MapReader;
import org.github.logof.zxtiled.io.PluginLogger;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.cutter.BasicTileCutter;
import org.github.logof.zxtiled.mapeditor.enums.EnemyEnum;
import org.github.logof.zxtiled.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

/**
 * The standard map reader for TMX files.
 */
public class XMLMapTransformer implements MapReader {
    private final EntityResolver entityResolver = new MapEntityResolver();
    private TileMap tileMap;
    private String xmlPath;
    @Setter
    private PluginLogger logger;

    public XMLMapTransformer() {
        logger = new PluginLogger();
    }

    private static String makeUrl(String filename) throws MalformedURLException {
        final String url;
        if (filename.indexOf("://") > 0 || filename.startsWith("file:")) {
            url = filename;
        } else {
            url = new File(filename).toURI().toString();
        }
        return url;
    }

    private static int reflectFindMethodByName(Class clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase(methodName)) {
                return i;
            }
        }
        return -1;
    }

    private static String getAttributeValue(Node node, String attribname) {
        final NamedNodeMap attributes = node.getAttributes();
        String value = null;
        if (attributes != null) {
            Node attribute = attributes.getNamedItem(attribname);
            if (attribute != null) {
                value = attribute.getNodeValue();
            }
        }
        return value;
    }

    private static int getAttribute(Node node, String attribname, int def) {
        final String attr = getAttributeValue(node, attribname);
        if (attr != null) {
            return Integer.parseInt(attr);
        } else {
            return def;
        }
    }

    private static String getAttribute(Node node, String attributeName, String def) {
        final String attr = getAttributeValue(node, attributeName);
        if (attr != null) {
            return attr;
        } else {
            return def;
        }
    }

    private static float getAttribute(Node node, String attribname, float def) {
        final String attr = getAttributeValue(node, attribname);
        if (attr != null) {
            return Float.parseFloat(attr);
        } else {
            return def;
        }
    }

    private static boolean getAttribute(Node node, String attribname, boolean def) {
        final String attr = getAttributeValue(node, attribname);
        if (attr != null) {
            return Boolean.parseBoolean(attr);
        } else {
            return def;
        }
    }

    /**
     * Reads properties from amongst the given children. When a "properties"
     * element is encountered, it recursively calls itself with the children
     * of this node. This function ensures backward compatibility with tmx
     * version 0.99a.
     * <p>
     * Support for reading property values stored as character data was added
     * in Tiled 0.7.0 (tmx version 0.99c).
     *
     * @param children the children amongst which to find properties
     * @param props    the properties object to set the properties of
     */
    private static void readProperties(NodeList children, Properties props) {
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("property".equalsIgnoreCase(child.getNodeName())) {
                final String key = getAttributeValue(child, "name");
                String value = getAttributeValue(child, "value");
                if (value == null) {
                    Node grandChild = child.getFirstChild();
                    if (grandChild != null) {
                        value = grandChild.getNodeValue();
                        if (value != null)
                            value = value.trim();
                    }
                }
                if (value != null)
                    props.setProperty(key, value);
            } else if ("properties".equals(child.getNodeName())) {
                readProperties(child.getChildNodes(), props);
            }
        }
    }

    private void reflectInvokeMethod(Object invokeVictim, Method method,
                                     String[] args) throws Exception {
        Class[] parameterTypes = method.getParameterTypes();
        Object[] conformingArguments = new Object[parameterTypes.length];

        if (args.length < parameterTypes.length) {
            throw new Exception("Insufficient arguments were supplied");
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if ("int".equalsIgnoreCase(parameterTypes[i].getName())) {
                conformingArguments[i] = Integer.valueOf(args[i]);
            } else if ("float".equalsIgnoreCase(parameterTypes[i].getName())) {
                conformingArguments[i] = Float.parseFloat(args[i]);
            } else if (parameterTypes[i].getName().endsWith("String")) {
                conformingArguments[i] = args[i];
            } else if ("boolean".equalsIgnoreCase(parameterTypes[i].getName())) {
                conformingArguments[i] = Boolean.valueOf(args[i]);
            } else {
                logger.debug("Unsupported argument type " +
                        parameterTypes[i].getName() +
                        ", defaulting to java.lang.String");
                conformingArguments[i] = args[i];
            }
        }

        method.invoke(invokeVictim, conformingArguments);
    }

    private void setMapType(String string) {
        if ("side_scrolled".equalsIgnoreCase(string)) {
            tileMap.setMapType(MapTypeEnum.MAP_SIDE_SCROLLED);
        } else {
            logger.warn("Unknown orientation '" + string + "'");
        }
    }

    private Object unmarshalClass(Class reflector, Node node) throws InstantiationException,
                                                                     IllegalAccessException,
                                                                     InvocationTargetException {
        Constructor<?> constructor = null;
        try {
            constructor = reflector.getConstructor(null);
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
            return null;
        }
        Object object = constructor.newInstance(null);
        Node n;

        Method[] methods = reflector.getMethods();
        NamedNodeMap attributes = node.getAttributes();

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                n = attributes.item(i);

                try {
                    int j = reflectFindMethodByName(reflector,
                            "set" + n.getNodeName());
                    if (j >= 0) {
                        reflectInvokeMethod(object, methods[j],
                                new String[]{n.getNodeValue()});
                    } else {
                        logger.warn("Unsupported attribute '" +
                                n.getNodeName() +
                                "' on <" + node.getNodeName() + "> tag");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    private Image unmarshalImage(Node t, String baseDir) throws IOException {
        Element e = ((Element) t);
        ImageHelper.ImageFormat imageFormat = ImageHelper.ImageFormat.valueOf(e.getAttribute("format")
                                                                               .toUpperCase(), ImageHelper.ImageFormat.PNG);
        Image img = null;

        String source = getAttributeValue(t, "source");

        if (source != null) {
            if (Util.checkRoot(source)) {
                source = makeUrl(source);
            } else {
                source = makeUrl(baseDir + source);
            }
            img = ImageIO.read(new URL(source));
            // todo: check whether external images would also be faster drawn
            // todo: from a scaled instance, see below
        } else {
            NodeList nl = t.getChildNodes();

            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if ("data".equals(node.getNodeName())) {
                    Node cdata = node.getFirstChild();
                    if (cdata == null) {
                        logger.warn("image <data> tag enclosed no " +
                                "data. (empty data tag)");
                    } else {
                        String sdata = cdata.getNodeValue();
                        byte[] imageData = Base64.getDecoder().decode(sdata.trim());

                        switch (imageFormat) {
                            case PNG: {
                                img = ImageHelper.pngToImage(imageData);
                            }
                            break;
                            case RAW: {
                                int width = Integer.parseInt(e.getAttribute("width"));
                                int height = Integer.parseInt(e.getAttribute("height"));
                                ImageHelper.PixelFormat pixelFormat = ImageHelper.PixelFormat.valueOf(e.getAttribute("pixelFormat"));
                                boolean bigEndian = e.getAttribute("byteOrder").equals("bigEndian");
                                img = ImageHelper.rawToImage(imageData, pixelFormat, bigEndian, width, height);
                            }
                            break;
                        }

                        // Deriving a scaled instance, even if it has the same
                        // size, somehow makes drawing of the tiles a lot
                        // faster on various systems (seen on Linux, Windows
                        // and MacOS X).
                        img = img.getScaledInstance(
                                img.getWidth(null), img.getHeight(null),
                                Image.SCALE_FAST);
                    }
                    break;
                }
            }
        }

        /*
        if (getAttributeValue(t, "set") != null) {
            TileSet ts = (TileSet)map.getTilesets().get(
                    Integer.parseInt(getAttributeValue(t, "set")));
            if (ts != null) {
                ts.addImage(img);
            }
        }
        */

        return img;
    }

    private Tileset unmarshalTilesetFile(InputStream in, String filename)
            throws Exception {
        Tileset set = null;
        Node tsNode;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            //builder.setErrorHandler(new XMLErrorHandler());
            Document tsDoc = builder.parse(in, ".");

            String xmlPathSave = xmlPath;
            if (filename.indexOf(File.separatorChar) >= 0) {
                xmlPath = filename.substring(0,
                        filename.lastIndexOf(File.separatorChar) + 1);
            }

            NodeList tsNodeList = tsDoc.getElementsByTagName("tileset");

            // There can be only one tileset in a .tsx file.
            tsNode = tsNodeList.item(0);
            if (tsNode != null) {
                set = unmarshalTileset(tsNode);
                if (set.getSource() != null) {
                    logger.warn("Recursive external Tilesets are not supported.");
                }
                set.setSource(filename);
            }

            xmlPath = xmlPathSave;
        } catch (SAXException e) {
            logger.error("Failed while loading " + filename + ": "
                    + e.getLocalizedMessage());
            //e.printStackTrace();
        }

        return set;
    }

    private Tileset unmarshalTileset(Node t) throws Exception {
        String source = getAttributeValue(t, "source");
        String basedir = getAttributeValue(t, "basedir");
        int firstGid = getAttribute(t, "firstgid", 1);

        String tilesetBaseDir = xmlPath;

        if (basedir != null) {
            tilesetBaseDir = basedir; //makeUrl(basedir);
        }

        if (source != null) {
            String filename = tilesetBaseDir + source;
            //if (Util.checkRoot(source)) {
            //    filename = makeUrl(source);
            //}

            Tileset ext = null;

            try {
                //just a little check for tricky people...
                String extention = source.substring(source.lastIndexOf('.') + 1);
                if (!"tsx".equalsIgnoreCase(extention)) {
                    logger.warn("tileset files should end in .tsx! (" + source + ")");
                }

                InputStream in = new URL(makeUrl(filename)).openStream();
                ext = unmarshalTilesetFile(in, filename);
            } catch (FileNotFoundException fnf) {
                logger.error("Could not find external tileset file " +
                        filename);
            }

            if (ext == null) {
                logger.error("tileset " + source + " was not loaded correctly!");
                ext = new Tileset();
            }

            ext.setFirstGid(firstGid);
            return ext;
        } else {
            Tileset set = new Tileset();

            set.setName(getAttributeValue(t, "name"));
            set.setBaseDir(basedir);
            set.setFirstGid(firstGid);

            boolean hasTilesetImage = false;
            NodeList children = t.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);

                if (child.getNodeName().equalsIgnoreCase("image")) {
                    if (hasTilesetImage) {
                        logger.warn("Ignoring illegal image element after tileset image.");
                        continue;
                    }

                    String imgSource = getAttributeValue(child, "source");
                    String id = getAttributeValue(child, "id");

                    if (imgSource != null && id == null) {
                        // Not a shared image, but an entire set in one image
                        // file. There should be only one image element in this
                        // case.
                        hasTilesetImage = true;

                        // FIXME: importTileBitmap does not fully support URLs
                        String sourcePath = imgSource;
                        if (!new File(imgSource).isAbsolute()) {
                            sourcePath = tilesetBaseDir + imgSource;
                        }

                        logger.info("Importing " + sourcePath + "...");
                        set.importTileBitmap(sourcePath, new BasicTileCutter());
                    } else {
                        Image image = unmarshalImage(child, tilesetBaseDir);
                        String idValue = getAttributeValue(child, "id");
                        int imageId = Integer.parseInt(idValue);
                        set.addImage(image, imageId, imgSource);
                    }
                } else if (child.getNodeName().equalsIgnoreCase("tile")) {
                    Tile tile = unmarshalTile(set, child, tilesetBaseDir);
                    if (!hasTilesetImage || tile.getId() > set.getMaxTileId()) {
                        set.addTile(tile);
                    } else {
                        Tile myTile = set.getTile(tile.getId());
                        myTile.setProperties(tile.getProperties());
                        //TODO: there is the possibility here of overlaying images,
                        //      which some people may want
                    }
                }
            }

            return set;
        }
    }

    private MapObject readMapObject(Node node) throws Exception {
        final String name = getAttributeValue(node, "name");
        final String typeString = getAttributeValue(node, "type");
        final int x = getAttribute(node, "x", 0);
        final int y = getAttribute(node, "y", 0);
        final int screenNumber = getAttribute(node, "screen", 0);

        final int speed = getAttribute(node, "speed", 1);
        final int moveByX = getAttribute(node, "moveByX", x);
        final int moveByY = getAttribute(node, "moveByY", y);

        MapObject obj = new MapObject(x, y, screenNumber);
        obj.setSpeed(speed);
        obj.setPath(new Rectangle(moveByX, moveByY));
        if (name != null) {
            obj.setName(name);
        }
        obj.setType(EnemyEnum.valueOf(typeString));

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("image".equalsIgnoreCase(child.getNodeName())) {
                String source = getAttributeValue(child, "source");
                if (source != null) {
                    if (!new File(source).isAbsolute()) {
                        source = xmlPath + source;
                    }
                    obj.setImageSource(source);
                }
                break;
            }
        }

        Properties props = new Properties();
        readProperties(children, props);

        obj.setProperties(props);
        return obj;
    }

    private Tile unmarshalTile(Tileset set, Node t, String baseDir)
            throws Exception {
        Tile tile = null;
        NodeList children = t.getChildNodes();
        try {
            tile = (Tile) unmarshalClass(Tile.class, t);
        } catch (Exception e) {
            logger.error("failed creating tile: " + e.getLocalizedMessage());
            return tile;
        }

        tile.setTileSet(set);

        readProperties(children, tile.getProperties());

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("image".equalsIgnoreCase(child.getNodeName())) {
                int id = getAttribute(child, "id", -1);
                String src = getAttribute(child, "source", null);
                Image img = unmarshalImage(child, baseDir);
                if (id < 0) {
                    id = set.addImage(img, src);
                }
                tile.setImage(id);
            } else if ("animation".equalsIgnoreCase(child.getNodeName())) {
                // TODO: fill this in once XMLMapWriter is complete
            }
        }

        return tile;
    }

    private MapLayer unmarshalObjectLayer(Node node) throws Exception {
        ObjectLayer objectLayer = null;
        try {
            objectLayer = (ObjectLayer) unmarshalClass(ObjectLayer.class, node);
        } catch (Exception e) {
            e.printStackTrace();
            return objectLayer;
        }

        if (Objects.isNull(objectLayer)) {
            return new ObjectLayer(new Rectangle(1, 1));
        }

        final int offsetX = getAttribute(node, "x", 0);
        final int offsetY = getAttribute(node, "y", 0);
        objectLayer.setOffset(offsetX, offsetY);

        // Add all objects from the objects group
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("object".equalsIgnoreCase(child.getNodeName())) {
                objectLayer.addObject(readMapObject(child));
            }
        }

        Properties props = new Properties();
        readProperties(children, props);
        objectLayer.setProperties(props);

        return objectLayer;
    }

    /**
     * Loads a map layer from a layer node.
     *
     * @param t the node representing the "layer" element
     * @return the loaded map layer
     * @throws Exception
     */
    private MapLayer readLayer(Node t, int mapWidth, int mapHeight) throws Exception {
        TileLayer tileLayer = new TileLayer(mapWidth, mapHeight);

        final int offsetX = getAttribute(t, "x", 0);
        final int offsetY = getAttribute(t, "y", 0);
        final int visible = getAttribute(t, "visible", 1);
        final float viewPlaneDistance = getAttribute(t, "viewPlaneDistance", 0.0f);
        final boolean viewPlaneInfinitelyFarAway = getAttribute(t, "viewPlaneInfinitelyFarAway", false);

        tileLayer.setName(getAttributeValue(t, "name"));

        readProperties(t.getChildNodes(), tileLayer.getProperties());

        for (Node child = t.getFirstChild(); child != null;
             child = child.getNextSibling()) {
            String nodeName = child.getNodeName();
            if ("data".equalsIgnoreCase(nodeName)) {
                String encoding = getAttributeValue(child, "encoding");

                if ("base64".equalsIgnoreCase(encoding)) {
                    Node cdata = child.getFirstChild();
                    if (cdata == null) {
                        logger.warn("layer <data> tag enclosed no data. (empty data tag)");
                    } else {
                        byte[] decode = Base64.getDecoder().decode(cdata.getNodeValue().trim());
                        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(decode);
                        String comp = getAttributeValue(child, "compression");

                        InputStream inputStream = ("gzip".equalsIgnoreCase(comp))
                                ? new GZIPInputStream(arrayInputStream)
                                : arrayInputStream;

                        for (int y = 0; y < tileLayer.getHeight(); y++) {
                            for (int x = 0; x < tileLayer.getWidth(); x++) {
                                int tileId = 0;
                                tileId |= inputStream.read();
                                tileId |= inputStream.read() << 8;
                                tileId |= inputStream.read() << 16;
                                tileId |= inputStream.read() << 24;

                                Tileset ts = tileMap.findTileSetForTileGID(tileId);
                                if (ts != null) {
                                    tileLayer.setTileAt(x, y,
                                            ts.getTile(tileId - ts.getFirstGid()));
                                } else {
                                    tileLayer.setTileAt(x, y, null);
                                }
                            }
                        }
                    }
                } else {
                    int x = 0, y = 0;
                    for (Node dataChild = child.getFirstChild();
                         dataChild != null;
                         dataChild = dataChild.getNextSibling()) {
                        if ("tile".equalsIgnoreCase(dataChild.getNodeName())) {
                            int tileId = getAttribute(dataChild, "gid", -1);
                            Tileset ts = tileMap.findTileSetForTileGID(tileId);
                            if (ts != null) {
                                tileLayer.setTileAt(x, y,
                                        ts.getTile(tileId - ts.getFirstGid()));
                            } else {
                                tileLayer.setTileAt(x, y, null);
                            }

                            x++;
                            if (x == tileLayer.getWidth()) {
                                x = 0;
                                y++;
                            }
                            if (y == tileLayer.getHeight()) {
                                break;
                            }
                        }
                    }
                }
            } else if ("tileproperties".equalsIgnoreCase(nodeName)) {
                for (Node tpn = child.getFirstChild();
                     tpn != null;
                     tpn = tpn.getNextSibling()) {
                    if ("tile".equalsIgnoreCase(tpn.getNodeName())) {
                        int x = getAttribute(tpn, "x", -1);
                        int y = getAttribute(tpn, "y", -1);

                        Properties tip = new Properties();

                        readProperties(tpn.getChildNodes(), tip);
                        tileLayer.setTileInstancePropertiesAt(x, y, tip);
                    }
                }
            }
        }

        // This is done at the end, otherwise the offset is applied during
        // the loading of the tiles.
        tileLayer.setOffset(offsetX, offsetY);

        // Invisible layers are automatically locked, so it is important to
        // set the layer to potentially invisible _after_ the layer data is
        // loaded.
        // todo: Shouldn't this be just a user interface feature, rather than
        // todo: something to keep in mind at this level?
        tileLayer.setVisible(visible == 1);

        tileLayer.setViewPlaneDistance(viewPlaneDistance);
        tileLayer.setViewPlaneInfinitelyFarAway(viewPlaneInfinitelyFarAway);

        return tileLayer;
    }

    private void buildMap(Document doc) throws Exception {
        Node item, mapNode;

        mapNode = doc.getDocumentElement();

        if (!"map".equals(mapNode.getNodeName())) {
            throw new Exception("Not a valid tmx map file.");
        }

        // Get the map dimensions and create the map
        int mapWidth = getAttribute(mapNode, "width", 0);
        int mapHeight = getAttribute(mapNode, "height", 0);

        if (mapWidth > 0 && mapHeight > 0) {
            tileMap = new TileMap(mapWidth, mapHeight);
        } else {
            // Maybe this map is still using the dimensions element
            NodeList l = doc.getElementsByTagName("dimensions");
            for (int i = 0; (item = l.item(i)) != null; i++) {
                if (item.getParentNode() == mapNode) {
                    mapWidth = getAttribute(item, "width", 0);
                    mapHeight = getAttribute(item, "height", 0);

                    if (mapWidth > 0 && mapHeight > 0) {
                        tileMap = new TileMap(mapWidth, mapHeight);
                    }
                }
            }
        }

        if (tileMap == null) {
            throw new Exception("Couldn't locate map dimensions.");
        }

        // Load other map attributes
        String mapType = getAttributeValue(mapNode, "type");
        if (mapType != null) {
            setMapType(mapType);
        } else {
            setMapType("side_scrolled");
        }

        // Load properties
        readProperties(mapNode.getChildNodes(), tileMap.getProperties());

        // Load tilesets first, in case order is munged
        NodeList l = doc.getElementsByTagName("tileset");
        for (int i = 0; (item = l.item(i)) != null; i++) {
            tileMap.addTileset(unmarshalTileset(item));
        }

        // Load the layers and objectgroups
        for (Node sibs = mapNode.getFirstChild(); sibs != null; sibs = sibs.getNextSibling()) {
            if ("layer".equals(sibs.getNodeName())) {
                MapLayer layer = readLayer(sibs, mapWidth, mapHeight);
                if (layer != null) {
                    tileMap.addLayer(layer);
                }
            } else if ("objectLayer".equals(sibs.getNodeName())) {
                MapLayer layer = unmarshalObjectLayer(sibs);
                if (layer != null) {
                    tileMap.addLayer(layer);
                }
            }
        }
    }

    private TileMap unmarshal(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc;
        try {
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            InputSource inputSource = new InputSource(inputStream);
            inputSource.setSystemId(xmlPath);
            inputSource.setEncoding("UTF-8");
            doc = builder.parse(inputSource);
        } catch (SAXException e) {
            e.printStackTrace();
            throw new Exception("Error while parsing map file: " +
                    e);
        }

        buildMap(doc);
        return tileMap;
    }


    // MapReader interface

    public TileMap readMap(String filename) throws Exception {
        xmlPath = filename.substring(0,
                filename.lastIndexOf(File.separatorChar) + 1);

        String xmlFile = makeUrl(filename);
        //xmlPath = makeUrl(xmlPath);

        URL url = new URL(xmlFile);
        InputStream inputStream = url.openStream();

        // Wrap with GZIP decoder for .tmx.gz files
        if (filename.endsWith(".gz")) {
            inputStream = new GZIPInputStream(inputStream);
        }

        TileMap unmarshalledTileMap = unmarshal(inputStream);
        unmarshalledTileMap.setFilename(filename);

        tileMap = null;

        return unmarshalledTileMap;
    }

    @Override
    public TileMap readMap(InputStream inputStream) throws Exception {
        xmlPath = makeUrl(".");

        //unmarshalledMap.setFilename(xmlFile)
        //
        return unmarshal(inputStream);
    }

    public Tileset readTileset(String filename) throws Exception {
        String xmlFile = filename;

        xmlPath = filename.substring(0,
                filename.lastIndexOf(File.separatorChar) + 1);

        xmlFile = makeUrl(xmlFile);
        xmlPath = makeUrl(xmlPath);

        URL url = new URL(xmlFile);
        return unmarshalTilesetFile(url.openStream(), filename);
    }

    public Tileset readTileset(InputStream in) throws Exception {
        // TODO: The MapReader interface should be changed...
        return unmarshalTilesetFile(in, ".");
    }

    /**
     * @see org.github.logof.zxtiled.io.PluggableMapIO#getFilter()
     */
    public String getFilter() throws Exception {
        return "*.tmx,*.tmx.gz,*.tsx";
    }

    public String getPluginPackage() {
        return "Tiled internal TMX reader/writer";
    }

    /**
     * @see org.github.logof.zxtiled.io.PluggableMapIO#getDescription()
     */
    public String getDescription() {
        return "This is the core Tiled TMX format reader\n" +
                "\n" +
                "Tiled Map Editor, (c) 2004-2008\n" +
                "Adam Turk\n" +
                "Bjorn Lindeijer";
    }

    public String getName() {
        return "Default Tiled XML (TMX) map reader";
    }

    public boolean accept(File pathname) {
        try {
            String path = pathname.getCanonicalPath();
            if (path.endsWith(".tmx") || path.endsWith(".tsx") ||
                    path.endsWith(".tmx.gz")) {
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }

    private class MapEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) {
            if (systemId.equals(Constants.DTD)) {
                return new InputSource(Resources.resourceResolver);
            }
            return null;
        }
    }
}
