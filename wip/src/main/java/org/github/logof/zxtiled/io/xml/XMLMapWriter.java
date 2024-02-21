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
import org.github.logof.zxtiled.core.ObjectLayer;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.core.objects.HotspotObject;
import org.github.logof.zxtiled.core.objects.MapObject;
import org.github.logof.zxtiled.core.objects.MovingObject;
import org.github.logof.zxtiled.core.objects.PlayerStartObject;
import org.github.logof.zxtiled.io.ImageHelper;
import org.github.logof.zxtiled.io.MapWriter;
import org.github.logof.zxtiled.io.PluginLogger;
import org.github.logof.zxtiled.mapeditor.Constants;
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
import java.util.Objects;
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

    private Preferences preferences = TiledConfiguration.node("saving");

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

    private static void writeObjectLayer(ObjectLayer objectLayer, XMLWriter writer, String wp) throws IOException {
        for (MovingObject movingObject : objectLayer.getEnemyList()) {
            writeMapObject(movingObject, writer, wp);
        }

        for (HotspotObject hotspotObject : objectLayer.getHotspotList()) {
            writeMapObject(hotspotObject, writer, wp);
        }

        writeMapObject(objectLayer.getPlayerStartObject(), writer, wp);
        writeMapObject(objectLayer.getPlayerFinishObject(), writer, wp);

    }

    private static void writeMapObject(MapObject mapObject, XMLWriter writer, String wp) throws IOException {
        if (Objects.isNull(mapObject)) {
            return;
        }

        if (mapObject instanceof MovingObject) {
            MovingObject movingObject = (MovingObject) mapObject;
            writer.startElement("object");
            writer.writeAttribute("name", movingObject.getName());
            writer.writeAttribute("type", movingObject.getType().name());
            writer.writeAttribute("x", movingObject.getCoordinateXAt());
            writer.writeAttribute("y", movingObject.getCoordinateYAt());
            writer.writeAttribute("screen", movingObject.getScreenNumber());
            writer.writeAttribute("speed", movingObject.getObjectSpeed());
            writer.writeAttribute("moveByX", movingObject.getFinalPoint().x);
            writer.writeAttribute("moveByY", movingObject.getFinalPoint().y);
            writeProperties(movingObject.getProperties(), writer);

            if (!movingObject.getImageSource().isEmpty()) {
                writer.startElement("image");
                writer.writeAttribute("source", getRelativePath(wp, movingObject.getImageSource()));
                writer.endElement();
            }
        } else if (mapObject instanceof HotspotObject) {
            HotspotObject hotspotObject = (HotspotObject) mapObject;
            writer.startElement("hotspot");
            writer.writeAttribute("type", hotspotObject.getType().name());
            writer.writeAttribute("x", hotspotObject.getCoordinateXAt());
            writer.writeAttribute("y", hotspotObject.getCoordinateYAt());
            writer.writeAttribute("screen", hotspotObject.getScreenNumber());
        } else {
            writer.startElement("point");
            writer.writeAttribute("type", (mapObject instanceof PlayerStartObject) ? "PLAYER_START" : "PLAYER_FINISH");
            writer.writeAttribute("x", mapObject.getCoordinateXAt());
            writer.writeAttribute("y", mapObject.getCoordinateYAt());
            writer.writeAttribute("screen", mapObject.getScreenNumber());
        }
        writer.endElement();
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
        if (!(new File(to)).isAbsolute()) {
            return to;
        }

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
        return preferences;
    }

    public void setPreferences(Preferences prefs) {
        this.preferences = prefs;
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
    public void writeTileset(Tileset set, String filename) throws Exception {
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

    public void writeTileset(Tileset set, OutputStream out) throws Exception {
        Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        XMLWriter xmlWriter = new XMLWriter(writer);

        xmlWriter.startDocument();
        writeTileset(set, xmlWriter, "/.");
        xmlWriter.endDocument();

        writer.flush();
    }

    private void writeMap(TileMap tileMap, XMLWriter writer, String wp) throws IOException {
        writer.writeDocType("map", null, Constants.DTD);
        writer.startElement("map");
        writer.writeAttribute("version", "1.0");
        writer.writeAttribute("type", tileMap.getMapType().getName());
        writer.writeAttribute("width", tileMap.getWidth());
        writer.writeAttribute("height", tileMap.getHeight());

        writeProperties(tileMap.getProperties(), writer);

        int firstgid = 1;
        for (Tileset tileset : tileMap.getTilesets()) {
            tileset.setFirstGid(firstgid);
            writeTilesetReference(tileset, writer, wp);
            firstgid += tileset.getMaxTileId() + 1;
        }

        if (preferences.getBoolean("encodeLayerData", true) && preferences.getBoolean("usefulComments", false)) {
            writer.writeComment("Layer data is " + (preferences.getBoolean("layerCompression", true) ? "compressed (GZip)" : "") + " binary data, encoded in Base64");
        }
        Iterator<MapLayer> mapLayers = tileMap.getLayers();
        while (mapLayers.hasNext()) {
            MapLayer layer = mapLayers.next();
            writeMapLayer(layer, writer, wp);
        }

        writer.endElement();
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
    private void writeTilesetReference(Tileset set, XMLWriter w, String wp)
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
        String imageFormatName = preferences.get("imageFormat", "PNG");
        String pixelFormatName = preferences.get("pixelFormat", "A8R8G8B8");
        boolean imageIsBigEndian = preferences.getBoolean("imageIsBigEndian", true);

        ImageHelper.ImageFormat imageFormat = ImageHelper.ImageFormat.valueOf(imageFormatName, ImageHelper.ImageFormat.PNG);
        ImageHelper.PixelFormat pixelFormat = ImageHelper.PixelFormat.valueOf(pixelFormatName, ImageHelper.PixelFormat.A8R8G8B8);

        w.startElement("image");
        if (id != -1) {
            w.writeAttribute("id", id);
        }

        if (imageSource != null) {
            w.writeAttribute("source", imageSource);
        }

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

    private void writeTileset(Tileset tileset, XMLWriter writer, String wp)
            throws IOException {

        String tileBmpFile = tileset.getTileBmpFile();
        String name = tileset.getName();

        writer.startElement("tileset");
        writer.writeAttribute("firstgid", tileset.getFirstGid());

        if (name != null) {
            writer.writeAttribute("name", name);
        }


        if (tileset.getBaseDir() != null) {
            writer.writeAttribute("basedir", tileset.getBaseDir());
        }

        if (tileBmpFile != null) {
            writer.startElement("image");
            writer.writeAttribute("source", getRelativePath(wp, tileBmpFile));
            writer.endElement();

            // Write tile properties when necessary.
            Iterator<Object> tileIterator = tileset.iterator();

            while (tileIterator.hasNext()) {
                Tile tile = (Tile) tileIterator.next();
                // todo: move the null check back into the iterator?
                if (tile != null && !tile.getProperties().isEmpty()) {
                    writer.startElement("tile");
                    writer.writeAttribute("id", tile.getId());
                    writeProperties(tile.getProperties(), writer);
                    writer.endElement();
                }
            }
        } else {
            // Embedded tileset

            // this determines whether or not to encode the image data in base64 and write it directly into the <image> tag (under <data>
            boolean embedImageData = preferences.getBoolean("embedImages", true);

            // determines if the tile tileset has a separate image list (true), or if each <image> appears inside the <tile> it belongs to
            boolean tileSetImages = preferences.getBoolean("tileSetImages", false);

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
                        writer.startElement("image");
                        writer.writeAttribute("source", getRelativePath(wp, imagePath));
                        writer.endElement();
                    } else {
                        writeEmbeddedImage(id, image, writer, tileset.getImageSource(id));
                    }
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
                        writeTile(tile, tileset, wp, writer);
                    }
                }
            }
        }
        writer.endElement();
    }

    /**
     * Writes this layer to an XMLWriter. This should be done <b>after</b> the
     * first global ids for the tilesets are determined, in order for the right
     * gids to be written to the layer data.
     */
    private void writeMapLayer(MapLayer mapLayer, XMLWriter xmlWriter, String wp) throws IOException {
        boolean encodeLayerData = preferences.getBoolean("encodeLayerData", true);
        boolean compressLayerData = preferences.getBoolean("layerCompression", true) && encodeLayerData;

        Rectangle bounds = mapLayer.getBounds();

        if (mapLayer instanceof ObjectLayer) {
            xmlWriter.startElement("objectLayer");
        } else {
            xmlWriter.startElement("layer");
        }

        xmlWriter.writeAttribute("name", mapLayer.getName());

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

        if (mapLayer instanceof ObjectLayer) {
            writeObjectLayer((ObjectLayer) mapLayer, xmlWriter, wp);
        } else if (mapLayer instanceof TileLayer) {
            final TileLayer tileLayer = (TileLayer) mapLayer;
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

            if (tilePropertiesElementStarted) {
                xmlWriter.endElement();
            }
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
    private void writeTile(Tile tile, Tileset set, String wp, XMLWriter w) throws IOException {
        w.startElement("tile");
        w.writeAttribute("id", tile.getId());

        //if (groundHeight != getHeight()) {
        //    w.writeAttribute("groundheight", "" + groundHeight);
        //}

        writeProperties(tile.getProperties(), w);

        boolean embedImages = preferences.getBoolean("embedImages", true);
        boolean tileSetImages = preferences.getBoolean("tileSetImages", false);
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
                    String prefix = preferences.get("tileImagePrefix", "tile");
                    String filename = prefix + tile.getId() + ".png";
                    String path = preferences.get("maplocation", "") + filename;
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
