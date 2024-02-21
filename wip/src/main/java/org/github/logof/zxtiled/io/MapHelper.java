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

package org.github.logof.zxtiled.io;

import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.io.c.HMapWriter;
import org.github.logof.zxtiled.io.xml.XMLMapTransformer;
import org.github.logof.zxtiled.io.xml.XMLMapWriter;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.io.IOException;

/**
 * A handler for saving and loading maps.
 */
public class MapHelper {
    public static final String ERROR_LOAD_MAP = Resources.getString("general.file.noload.map");
    public static final String ERROR_LOAD_TILESET = Resources.getString("general.file.noload.tileset");


    public static void init() {
    }

    /**
     * Saves the current map. Use the extension (.xxx) of the filename to
     * determine the plugin to use when writing the file. Throws an exception
     * when the extension is not supported by either the TMX writer or a
     * plugin. (Unlikely)
     *
     * @param filename       filename to save the current map to
     * @param currentTileMap {@link TileMap} instance to save to the file
     * @throws Exception
     * @see MapWriter#writeMap(TileMap, String)
     */
    public static void saveMap(TileMap currentTileMap, String filename) throws Exception {
        MapWriter mapWriter;
        if (filename.endsWith(".tmx") || filename.endsWith(".tmx.gz")) {
            // Override, so people can't overtake our format
            mapWriter = new XMLMapWriter();
        } else if (filename.endsWith(".h")) {
            mapWriter = new HMapWriter();
        } else {
            throw new RuntimeException("Not save map");
        }

        PluginLogger logger = new PluginLogger();
        mapWriter.setLogger(logger);
        mapWriter.writeMap(currentTileMap, filename);
        currentTileMap.setFilename(filename);

    }

    /**
     * Saves a tileset.  Use the extension (.xxx) of the filename to determine
     * the plugin to use when writing the file. Throws an exception when the
     * extension is not supported by either the TMX writer or a plugin.
     *
     * @param filename Filename to save the tileset to.
     * @param set      The TileSet instance to save to the file
     * @throws Exception
     * @see MapWriter#writeTileset(Tileset, String)
     */
    public static void saveTileset(Tileset set, String filename)
            throws Exception {
        MapWriter mapWriter;
        if (filename.endsWith(".tsx")) {
            // Override, so people can't overtake our format
            mapWriter = new XMLMapWriter();
        } else {
            throw new RuntimeException("Not save tileset");
        }

        PluginLogger logger = new PluginLogger();
        mapWriter.setLogger(logger);
        mapWriter.writeTileset(set, filename);
        set.setSource(filename);
    }

    /**
     * Saves a map. Ignores the extension of the filename, and instead uses the
     * passed plugin to write the file. Plugins can still refuse to save the file
     * based on the extension, but this is not recommended practice.
     *
     * @param currentTileMap
     * @param pmio
     * @param filename
     * @throws Exception
     */
    public static void saveMap(TileMap currentTileMap, PluggableMapIO pmio, String filename)
            throws Exception {
        MapWriter mw = (MapWriter) pmio;

        PluginLogger logger = new PluginLogger();
        mw.setLogger(logger);
        mw.writeMap(currentTileMap, filename);
        currentTileMap.setFilename(filename);
    }

    /**
     * Loads a map. Use the extension (.xxx) of the filename to determine
     * the plugin to use when reading the file. Throws an exception when the
     * extension is not supported by either the TMX writer or a plugin.
     *
     * @param file filename of map to load
     * @return a new Map, loaded from the specified file by a plugin
     * @throws Exception
     * @see MapReader#readMap(String)
     */
    public static TileMap loadMap(String file) throws Exception {
        TileMap ret = null;
        try {
            MapReader mapReader;
            if (file.endsWith(".tmx") || file.endsWith(".tmx.gz")) {
                // Override, so people can't overtake our format
                mapReader = new XMLMapTransformer();
            } else {
                throw new RuntimeException("NOT LOAD MAP");
            }

            PluginLogger logger = new PluginLogger();
            mapReader.setLogger(logger);
            ret = mapReader.readMap(file);
            ret.setFilename(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage() + (e.getCause() != null ? "\nCause: " +
                            e.getCause().getMessage() : ""),
                    ERROR_LOAD_MAP,
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error while loading " + file + ": " +
                            e.getMessage() + (e.getCause() != null ? "\nCause: " +
                            e.getCause().getMessage() : ""),
                    ERROR_LOAD_MAP,
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Loads a tileset. Use the extension (.xxx) of the filename to determine
     * the plugin to use when reading the file. Throws an exception when the
     * extension is not supported by either the TMX writer or a plugin.
     *
     * @param file filename of map to load
     * @return A new TileSet, loaded from the specified file by a plugin
     * @throws Exception
     * @see MapReader#readTileset(String)
     */
    public static Tileset loadTileset(String file) throws Exception {
        Tileset ret = null;
        try {
            MapReader mapReader;
            if (file.endsWith(".tsx")) {
                // Override, so people can't overtake our format
                mapReader = new XMLMapTransformer();
            } else {
                throw new RuntimeException("Not load tileset");
            }

            PluginLogger logger = new PluginLogger();
            mapReader.setLogger(logger);
            ret = mapReader.readTileset(file);
            ret.setSource(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage() + (e.getCause() != null ? "\nCause: " +
                            e.getCause().getMessage() : ""),
                    ERROR_LOAD_TILESET,
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error while loading " + file + ": " +
                            e.getMessage() + (e.getCause() != null ? "\nCause: " +
                            e.getCause().getMessage() : ""),
                    ERROR_LOAD_TILESET,
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return ret;
    }
}
