/*
 *  Tiled Map Editor, (c) 2004-2008
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

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.core.ObjectsLayer;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.TileSet;
import org.github.logof.zxtiled.io.ImageHelper;
import org.github.logof.zxtiled.io.MapWriter;
import org.github.logof.zxtiled.io.PluginLogger;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import org.github.logof.zxtiled.util.TiledConfiguration;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.zip.GZIPOutputStream;

/**
 * A writer for Tiled's TMX map format.
 */
public class XMLMapWriter implements MapWriter {
    private static final int LAST_BYTE = 0x000000FF;

    private Preferences prefs = TiledConfiguration.node("saving");

    private static void writeProperties(Properties props, XMLWriter w) throws
            IOException {
        if (!props.isEmpty()) {
            final SortedSet<Object> propertyKeys = new TreeSet<>();
            propertyKeys.addAll(props.keySet());
            w.startElement("properties");
            for (Object propertyKey : propertyKeys) {
                final String key = (String) propertyKey;
                final String property = props.getProperty(key);
                w.startElement("property");
                w.writeAttribute("name", key);
                if (property.indexOf('\n') == -1) {
                    w.writeAttribute("value", property);
                } else {
                    // Save multiline values as character data
                    w.writeCDATA(property);
                }
                w.endElement();
            }
            w.endElement();
        }
    }

    private static void writeObjectGroup(ObjectsLayer o, XMLWriter w, String wp)
            throws IOException {
        Iterator<MapObject> itr = o.getObjects();
        while (itr.hasNext()) {
            writeMapObject(itr.next(), w, wp);
        }
    }

    private static void writeMapObject(MapObject mapObject, XMLWriter w, String wp)
            throws IOException {
        w.startElement("object");
        w.writeAttribute("name", mapObject.getName());

        if (!mapObject.getType().isEmpty()) {
            w.writeAttribute("type", mapObject.getType());
        }

        w.writeAttribute("x", mapObject.getX());
        w.writeAttribute("y", mapObject.getY());

        if (mapObject.getWidth() != 0) {
            w.writeAttribute("width", mapObject.getWidth());
        }
        if (mapObject.getHeight() != 0) {
            w.writeAttribute("height", mapObject.getHeight());
        }

        writeProperties(mapObject.getProperties(), w);

        if (!mapObject.getImageSource().isEmpty()) {
            w.startElement("image");
            w.writeAttribute("source",
                    getRelativePath(wp, mapObject.getImageSource()));
            w.endElement();
        }

        w.endElement();
    }

    /**
     * Returns the relative path from one file to the other. The function
     * expects absolute paths, relative paths will be converted to absolute
     * using the working directory.
     *
     * @param from the path of the origin file
     * @param to   the path of the destination file
     * @return the relative path from origin to destination
     */
    public static String getRelativePath(String from, String to) {
        if (!(new File(to)).isAbsolute())
            return to;

        // Make the two paths absolute and unique
        try {
            from = new File(from).getCanonicalPath();
            to = new File(to).getCanonicalPath();
        } catch (IOException e) {
        }

        File fromFile = new File(from);
        File toFile = new File(to);
        Vector<String> fromParents = new Vector<>();
        Vector<String> toParents = new Vector<>();

        // Iterate to find both parent lists
        while (fromFile != null) {
            fromParents.add(0, fromFile.getName());
            fromFile = fromFile.getParentFile();
        }
        while (toFile != null) {
            toParents.add(0, toFile.getName());
            toFile = toFile.getParentFile();
        }

        // Iterate while parents are the same
        int shared;
        int maxShared = Math.min(fromParents.size(), toParents.size());
        for (shared = 0; shared < maxShared; shared++) {
            String fromParent = fromParents.get(shared);
            String toParent = toParents.get(shared);
            if (!fromParent.equals(toParent)) {
                break;
            }
        }

        // Append .. for each remaining parent in fromParents
        StringBuilder relPathBuf = new StringBuilder();
        relPathBuf.append((".." + File.separator).repeat(Math.max(0, fromParents.size() - 1 - shared)));

        // Add the remaining part in toParents
        for (int i = shared; i < toParents.size() - 1; i++) {
            relPathBuf.append(toParents.get(i)).append(File.separator);
        }
        relPathBuf.append(new File(to).getName());
        String relPath = relPathBuf.toString();

        // Turn around the slashes when path is relative
        try {
            String absPath = new File(relPath).getCanonicalPath();

            if (!absPath.equals(relPath)) {
                // Path is not absolute, turn slashes around
                // Assumes: \ does not occur in filenames
                relPath = relPath.replace('\\', '/');
            }
        } catch (IOException ignored) {
        }

        return relPath;
    }

    public Preferences getPreferences() {
        return prefs;
    }

    public void setPreferences(Preferences prefs) {
        this.prefs = prefs;
    }

    /**
     * Saves a map to an XML file.
     *
     * @param filename the filename of the map file
     */
    public void writeMap(TileMap tileMap, String filename) throws Exception {
        OutputStream os = new FileOutputStream(filename);

        if (filename.endsWith(".tmx.gz")) {
            os = new GZIPOutputStream(os);
        }

        Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        XMLWriter xmlWriter = new XMLWriter(writer);

        xmlWriter.startDocument();
        writeMap(tileMap, xmlWriter, filename);
        xmlWriter.endDocument();

        writer.flush();

        if (os instanceof GZIPOutputStream) {
            ((GZIPOutputStream) os).finish();
        }

        os.close();
    }

    /**
     * Saves a tileset to an XML file.
     *
     * @param filename the filename of the tileset file
     */
    public void writeTileset(TileSet set, String filename) throws Exception {
        OutputStream os = new FileOutputStream(filename);
        Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        XMLWriter xmlWriter = new XMLWriter(writer);

        xmlWriter.startDocument();
        writeTileset(set, xmlWriter, filename);
        xmlWriter.endDocument();

        writer.flush();
    }

    public void writeMap(TileMap tileMap, OutputStream out) throws Exception {
        Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        XMLWriter xmlWriter = new XMLWriter(writer);

        xmlWriter.startDocument();
        writeMap(tileMap, xmlWriter, "/.");
        xmlWriter.endDocument();

        writer.flush();
    }

    public void writeTileset(TileSet set, OutputStream out) throws Exception {
        Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        XMLWriter xmlWriter = new XMLWriter(writer);

        xmlWriter.startDocument();
        writeTileset(set, xmlWriter, "/.");
        xmlWriter.endDocument();

        writer.flush();
    }

    private void writeMap(TileMap tileMap, XMLWriter w, String wp) throws IOException {
        w.writeDocType("map", null, "http://mapeditor.org/dtd/1.0/map.dtd");
        w.startElement("map");

        w.writeAttribute("version", "1.0");

        if (tileMap.getOrientation() == TileMap.MDO_ORTHOGONAL) {
            w.writeAttribute("orientation", "orthogonal");
        }

        w.writeAttribute("width", tileMap.getWidth());
        w.writeAttribute("height", tileMap.getHeight());
        w.writeAttribute("tilewidth", tileMap.getTileWidth());
        w.writeAttribute("tileheight", tileMap.getTileHeight());

        w.writeAttribute("eyeDistance", tileMap.getEyeDistance());
        w.writeAttribute("viewportWidth", tileMap.getViewportWidth());
        w.writeAttribute("viewportHeight", tileMap.getViewportHeight());

        writeProperties(tileMap.getProperties(), w);

        int firstgid = 1;
        for (TileSet tileset : tileMap.getTilesets()) {
            tileset.setFirstGid(firstgid);
            writeTilesetReference(tileset, w, wp);
            firstgid += tileset.getMaxTileId() + 1;
        }

        if (prefs.getBoolean("encodeLayerData", true) && prefs.getBoolean("usefulComments", false))
            w.writeComment("Layer data is " + (prefs.getBoolean("layerCompression", true) ? "compressed (GZip)" : "") + " binary data, encoded in Base64");
        Iterator<MapLayer> ml = tileMap.getLayers();
        while (ml.hasNext()) {
            MapLayer layer = ml.next();
            writeMapLayer(layer, w, wp);
        }

        w.endElement();
    }

    /**
     * Writes a reference to an external tileset into a XML document. In the
     * case where the tileset is not stored in an external file, writes the
     * contents of the tileset instead.
     *
     * @param set the tileset to write a reference to
     * @param w   the XML writer to write to
     * @param wp  the working directory of the map
     * @throws java.io.IOException
     */
    private void writeTilesetReference(TileSet set, XMLWriter w, String wp)
            throws IOException {

        String source = set.getSource();

        if (source == null) {
            writeTileset(set, w, wp);
        } else {
            w.startElement("tileset");
            w.writeAttribute("firstgid", set.getFirstGid());
            w.writeAttribute("source", getRelativePath(wp, source));
            if (set.getBaseDir() != null) {
                w.writeAttribute("basedir", set.getBaseDir());
            }
            w.endElement();
        }
    }

    private void writeEmbeddedImage(int id, Image image, XMLWriter w, String imageSource) throws IOException {
        String imageFormatName = prefs.get("imageFormat", "PNG");
        String pixelFormatName = prefs.get("pixelFormat", "A8R8G8B8");
        boolean imageIsBigEndian = prefs.getBoolean("imageIsBigEndian", true);

        ImageHelper.ImageFormat imageFormat = ImageHelper.ImageFormat.valueOf(imageFormatName, ImageHelper.ImageFormat.PNG);
        ImageHelper.PixelFormat pixelFormat = ImageHelper.PixelFormat.valueOf(pixelFormatName, ImageHelper.PixelFormat.A8R8G8B8);

        w.startElement("image");
        if (id != -1)
            w.writeAttribute("id", id);

        if (imageSource != null)
            w.writeAttribute("source", imageSource);

        w.writeAttribute("format", imageFormat.toString().toLowerCase());

        switch (imageFormat) {
            default:
            case PNG:
                w.startElement("data");
                w.writeAttribute("encoding", "base64");
                w.writeCDATA(new String(Base64.getEncoder().encode(ImageHelper.imageToPNG(image))));
                w.endElement();
                break;
            case RAW:
                w.writeAttribute("pixelFormat", pixelFormat.toString());
                w.writeAttribute("byteOrder", imageIsBigEndian ? "bigEndian" : "littleEndian");
                w.writeAttribute("width", ImageHelper.getImageWidth(image));
                w.writeAttribute("height", ImageHelper.getImageHeight(image));
                w.startElement("data");
                w.writeAttribute("encoding", "base64");
                w.writeCDATA(new String(Base64.getEncoder()
                                              .encode(ImageHelper.imageToRAW(image, pixelFormat, imageIsBigEndian))));
                w.endElement();
                break;
        }
        w.endElement();
    }

    private void writeTileset(TileSet tileset, XMLWriter w, String wp)
            throws IOException {

        String tileBmpFile = tileset.getTilebmpFile();
        String name = tileset.getName();

        w.startElement("tileset");
        w.writeAttribute("firstgid", tileset.getFirstGid());

        if (name != null) {
            w.writeAttribute("name", name);
        }

        if (tileBmpFile != null) {
            w.writeAttribute("tilewidth", tileset.getTileWidth());
            w.writeAttribute("tileheight", tileset.getTileHeight());
        }

        if (tileset.getBaseDir() != null) {
            w.writeAttribute("basedir", tileset.getBaseDir());
        }

        if (tileBmpFile != null) {
            w.startElement("image");
            w.writeAttribute("source", getRelativePath(wp, tileBmpFile));

            Color trans = tileset.getTransparentColor();
            if (trans != null) {
                w.writeAttribute("trans", Integer.toHexString(
                        trans.getRGB()).substring(2));
            }
            w.endElement();

            // Write tile properties when necessary.
            Iterator<Object> tileIterator = tileset.iterator();

            while (tileIterator.hasNext()) {
                Tile tile = (Tile) tileIterator.next();
                // todo: move the null check back into the iterator?
                if (tile != null && !tile.getProperties().isEmpty()) {
                    w.startElement("tile");
                    w.writeAttribute("id", tile.getId());
                    writeProperties(tile.getProperties(), w);
                    w.endElement();
                }
            }
        } else {
            // Embedded tileset

            // this determines whether or not to encode the image data in base64 and write it directly into the <image> tag (under <data>
            boolean embedImageData = prefs.getBoolean("embedImages", true);

            // determines if the tile tileset has a separate image list (true), or if each <image> appears inside the <tile> it belongs to
            boolean tileSetImages = prefs.getBoolean("tileSetImages", false);

            if (tileSetImages) {

                // in this section, a tileset in the form of
                // <tileset ...>
                //    <tile id='..'...>
                //       <image>
                //         ....
                // is produced. embedImageData
                Enumeration<String> ids = tileset.getImageIds();
                while (ids.hasMoreElements()) {
                    String idString = ids.nextElement();
                    int id = Integer.parseInt(idString);
                    Image image = tileset.getImageById(id);
                    String imagePath = tileset.getImageSource(id);

                    // if images are not to be embedded, we need the actual source
                    // path for that image. If we can't get hold of that, we'll
                    // need to embed it regardless...
                    if (!embedImageData && imagePath != null) {
                        w.startElement("image");
                        w.writeAttribute("source", getRelativePath(wp, imagePath));
                        w.endElement();
                    } else
                        writeEmbeddedImage(id, image, w, tileset.getImageSource(id));
                }
            }

            // Check to see if there is a need to write tile elements
            Iterator<Tile> tileIterator = tileset.iterator();
            boolean needWrite = !tileset.isOneForOne();

            if (!tileSetImages) {
                needWrite = true;
            } else {
                // As long as one has properties, they all need to be written.
                // TODO: This shouldn't be necessary
                while (tileIterator.hasNext()) {
                    Tile tile = tileIterator.next();
                    if (!tile.getProperties().isEmpty()) {
                        needWrite = true;
                        break;
                    }
                }
            }

            if (needWrite) {
                tileIterator = tileset.iterator();
                while (tileIterator.hasNext()) {
                    Tile tile = tileIterator.next();
                    // todo: move this check back into the iterator?
                    if (tile != null) {
                        writeTile(tile, tileset, wp, w);
                    }
                }
            }
        }
        w.endElement();
    }

    /**
     * Writes this layer to an XMLWriter. This should be done <b>after</b> the
     * first global ids for the tilesets are determined, in order for the right
     * gids to be written to the layer data.
     */
    private void writeMapLayer(MapLayer mapLayer, XMLWriter xmlWriter, String wp) throws IOException {
        boolean encodeLayerData =
                prefs.getBoolean("encodeLayerData", true);
        boolean compressLayerData =
                prefs.getBoolean("layerCompression", true) &&
                        encodeLayerData;

        Rectangle bounds = mapLayer.getBounds();

        if (mapLayer.getClass() == SelectionLayer.class) {
            xmlWriter.startElement("selection");
        } else if (mapLayer instanceof ObjectsLayer) {
            xmlWriter.startElement("objectgroup");
        } else {
            xmlWriter.startElement("layer");
        }

        xmlWriter.writeAttribute("name", mapLayer.getName());
        xmlWriter.writeAttribute("width", bounds.width);
        xmlWriter.writeAttribute("height", bounds.height);
        xmlWriter.writeAttribute("viewPlaneDistance", mapLayer.getViewPlaneDistance());
        xmlWriter.writeAttribute("viewPlaneInfinitelyFarAway", mapLayer.isViewPlaneInfinitelyFarAway());

        if (bounds.x != 0) {
            xmlWriter.writeAttribute("x", bounds.x);
        }
        if (bounds.y != 0) {
            xmlWriter.writeAttribute("y", bounds.y);
        }

        if (!mapLayer.isVisible()) {
            xmlWriter.writeAttribute("visible", "0");
        }

        writeProperties(mapLayer.getProperties(), xmlWriter);

        if (mapLayer instanceof ObjectsLayer) {
            writeObjectGroup((ObjectsLayer) mapLayer, xmlWriter, wp);
        } else if (mapLayer instanceof TileLayer) {
            final TileLayer tileLayer = (TileLayer) mapLayer;
            xmlWriter.writeAttribute("tileWidth", tileLayer.getTileWidth());
            xmlWriter.writeAttribute("tileHeight", tileLayer.getTileHeight());
            xmlWriter.startElement("data");
            if (encodeLayerData) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                OutputStream outputStream;

                xmlWriter.writeAttribute("encoding", "base64");

                if (compressLayerData) {
                    xmlWriter.writeAttribute("compression", "gzip");
                    outputStream = new GZIPOutputStream(byteArrayOutputStream);
                } else {
                    outputStream = byteArrayOutputStream;
                }

                for (int y = 0; y < mapLayer.getHeight(); y++) {
                    for (int x = 0; x < mapLayer.getWidth(); x++) {
                        Tile tile = tileLayer.getTileAt(x + bounds.x,
                                y + bounds.y);
                        int gid = 0;

                        if (tile != null) {
                            gid = tile.getGid();
                        }

                        outputStream.write(gid & LAST_BYTE);
                        outputStream.write(gid >> 8 & LAST_BYTE);
                        outputStream.write(gid >> 16 & LAST_BYTE);
                        outputStream.write(gid >> 24 & LAST_BYTE);
                    }
                }

                if (compressLayerData) {
                    ((GZIPOutputStream) outputStream).finish();
                }

                xmlWriter.writeCDATA(new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray())));
            } else {
                for (int y = 0; y < mapLayer.getHeight(); y++) {
                    for (int x = 0; x < mapLayer.getWidth(); x++) {
                        Tile tile = tileLayer.getTileAt(x + bounds.x, y + bounds.y);
                        int gid = 0;

                        if (tile != null) {
                            gid = tile.getGid();
                        }

                        xmlWriter.startElement("tile");
                        xmlWriter.writeAttribute("gid", gid);
                        xmlWriter.endElement();
                    }
                }
            }
            xmlWriter.endElement();

            boolean tilePropertiesElementStarted = false;

            for (int y = 0; y < mapLayer.getHeight(); y++) {
                for (int x = 0; x < mapLayer.getWidth(); x++) {
                    Properties tip = tileLayer.getTileInstancePropertiesAt(x, y);

                    if (tip != null && !tip.isEmpty()) {
                        if (!tilePropertiesElementStarted) {
                            xmlWriter.startElement("tileproperties");
                            tilePropertiesElementStarted = true;
                        }
                        xmlWriter.startElement("tile");

                        xmlWriter.writeAttribute("x", x);
                        xmlWriter.writeAttribute("y", y);

                        writeProperties(tip, xmlWriter);

                        xmlWriter.endElement();
                    }
                }
            }

            if (tilePropertiesElementStarted)
                xmlWriter.endElement();
        }
        xmlWriter.endElement();
    }

    /**
     * Used to write tile elements for tilesets not based on a tileset image.
     *
     * @param tile the tile instance that should be written
     * @param w    the writer to write to
     * @throws IOException when an io error occurs
     */
    private void writeTile(Tile tile, TileSet set, String wp, XMLWriter w) throws IOException {
        w.startElement("tile");
        w.writeAttribute("id", tile.getId());

        //if (groundHeight != getHeight()) {
        //    w.writeAttribute("groundheight", "" + groundHeight);
        //}

        writeProperties(tile.getProperties(), w);

        boolean embedImages = prefs.getBoolean("embedImages", true);
        boolean tileSetImages = prefs.getBoolean("tileSetImages", false);
        Image tileImage = tile.getImage();

        // Write encoded data
        if (tileImage != null) {
            if (embedImages && !tileSetImages) {
                writeEmbeddedImage(-1, tileImage, w, set.getImageSource(tile.getImageId()));
            } else if (embedImages && tileSetImages) {
                w.startElement("image");
                w.writeAttribute("id", tile.getImageId());
                w.endElement();
            } else {
                String imageSource = set.getImageSource(tile.getImageId());
                w.startElement("image");
                if (imageSource != null) {
                    w.writeAttribute("source", getRelativePath(wp, imageSource));
                } else {
                    // if we have no source location given, write the images
                    // to where the map is
                    String prefix = prefs.get("tileImagePrefix", "tile");
                    String filename = prefix + tile.getId() + ".png";
                    String path = prefs.get("maplocation", "") + filename;
                    w.writeAttribute("source", filename);
                    FileOutputStream fw = new FileOutputStream(path);
                    byte[] data = ImageHelper.imageToPNG(tileImage);
                    fw.write(data, 0, data.length);
                    fw.close();
                }
                w.endElement();
            }
        }

        w.endElement();
    }

    /**
     * @see org.github.logof.zxtiled.io.PluggableMapIO#getFilter()
     */
    public String getFilter() throws Exception {
        return "*.tmx,*.tsx,*.tmx.gz";
    }

    public String getPluginPackage() {
        return "Tiled internal TMX reader/writer";
    }

    public String getDescription() {
        return
                "The core Tiled TMX format writer\n" +
                        "\n" +
                        "Tiled Map Editor, (c) 2004-2008\n" +
                        "Adam Turk\n" +
                        "Bjorn Lindeijer";
    }

    public String getName() {
        return "Default Tiled XML (TMX) map writer";
    }

    public boolean accept(File pathname) {
        try {
            String path = pathname.getCanonicalPath();
            if (path.endsWith(".tmx") || path.endsWith(".tsx") || path.endsWith(".tmx.gz")) {
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }

    public void setLogger(PluginLogger logger) {
    }
}
