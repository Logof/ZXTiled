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

import org.github.logof.zxtiled.core.Map;
import org.github.logof.zxtiled.core.TileSet;
import org.github.logof.zxtiled.io.c.HMapWriter;
import org.github.logof.zxtiled.io.xml.XMLMapTransformer;
import org.github.logof.zxtiled.io.xml.XMLMapWriter;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.dialogs.PluginLogDialog;
import org.github.logof.zxtiled.mapeditor.plugin.PluginClassLoader;
import org.github.logof.zxtiled.util.TiledConfiguration;
import javax.swing.*;
import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * A handler for saving and loading maps.
 */
public class MapHelper {
    public static final String ERROR_LOAD_MAP = Resources.getString("general.file.noload.map");
    public static final String ERROR_LOAD_TILESET = Resources.getString("general.file.noload.tileset");
    private static PluginClassLoader pluginLoader;

    /**
     * Called to tell the MapHelper which {@link PluginClassLoader} to use when
     * finding a suitable plugin for a filename.
     *
     * @param p the PluginClassLoader instance to use
     */
    public static void init(PluginClassLoader p) {
        pluginLoader = p;
    }

    /**
     * Saves the current map. Use the extension (.xxx) of the filename to
     * determine the plugin to use when writing the file. Throws an exception
     * when the extension is not supported by either the TMX writer or a
     * plugin. (Unlikely)
     *
     * @param filename   filename to save the current map to
     * @param currentMap {@link Map} instance to save to the file
     * @throws Exception
     * @see MapWriter#writeMap(Map, String)
     */
    public static void saveMap(Map currentMap, String filename) throws Exception {
        MapWriter mapWriter;
        if (filename.endsWith(".tmx") || filename.endsWith(".tmx.gz")) {
            // Override, so people can't overtake our format
            mapWriter = new XMLMapWriter();
        } else if (filename.endsWith(".h")) {
            mapWriter = new HMapWriter();
        } else {
            mapWriter = (MapWriter) pluginLoader.getWriterFor(filename);
        }

        PluginLogger logger = new PluginLogger();
        mapWriter.setLogger(logger);
        mapWriter.writeMap(currentMap, filename);
        currentMap.setFilename(filename);
        reportPluginMessages(logger);
    }

    /**
     * Saves a tileset.  Use the extension (.xxx) of the filename to determine
     * the plugin to use when writing the file. Throws an exception when the
     * extension is not supported by either the TMX writer or a plugin.
     *
     * @param filename Filename to save the tileset to.
     * @param set      The TileSet instance to save to the file
     * @throws Exception
     * @see MapWriter#writeTileset(TileSet, String)
     */
    public static void saveTileset(TileSet set, String filename)
            throws Exception {
        MapWriter mw;
        if (filename.endsWith(".tsx")) {
            // Override, so people can't overtake our format
            mw = new XMLMapWriter();
        } else {
            mw = (MapWriter) pluginLoader.getWriterFor(filename);
        }

        if (mw != null) {
            PluginLogger logger = new PluginLogger();
            mw.setLogger(logger);
            mw.writeTileset(set, filename);
            set.setSource(filename);
            reportPluginMessages(logger);
        } else {
            throw new Exception("Unsupported tileset format");
        }
    }

    /**
     * Saves a map. Ignores the extension of the filename, and instead uses the
     * passed plugin to write the file. Plugins can still refuse to save the file
     * based on the extension, but this is not recommended practice.
     *
     * @param currentMap
     * @param pmio
     * @param filename
     * @throws Exception
     */
    public static void saveMap(Map currentMap, PluggableMapIO pmio, String filename)
            throws Exception {
        MapWriter mw = (MapWriter) pmio;

        PluginLogger logger = new PluginLogger();
        mw.setLogger(logger);
        mw.writeMap(currentMap, filename);
        currentMap.setFilename(filename);
        reportPluginMessages(logger);
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
    public static Map loadMap(String file) throws Exception {
        Map ret = null;
        try {
            MapReader mr;
            if (file.endsWith(".tmx") || file.endsWith(".tmx.gz")) {
                // Override, so people can't overtake our format
                mr = new XMLMapTransformer();
            } else {
                mr = (MapReader) pluginLoader.getReaderFor(file);
            }

            if (mr != null) {
                PluginLogger logger = new PluginLogger();
                mr.setLogger(logger);
                ret = mr.readMap(file);
                ret.setFilename(file);
                reportPluginMessages(logger);
            } else {
                throw new Exception("Unsupported map format");
            }
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
    public static TileSet loadTileset(String file) throws Exception {
        TileSet ret = null;
        try {
            MapReader mr;
            if (file.endsWith(".tsx")) {
                // Override, so people can't overtake our format
                mr = new XMLMapTransformer();
            } else {
                mr = (MapReader) pluginLoader.getReaderFor(file);
            }

            if (mr != null) {
                PluginLogger logger = new PluginLogger();
                mr.setLogger(logger);
                ret = mr.readTileset(file);
                ret.setSource(file);
                reportPluginMessages(logger);
            } else {
                throw new Exception("Unsupported tileset format");
            }
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

    /**
     * Reports messages from the plugin to the user in a dialog.
     *
     * @param logger A Stack which was used by the plugin to record any messages it
     *               had for the user
     */
    private static void reportPluginMessages(PluginLogger logger) {
        // TODO: maybe have a nice dialog with a scrollbar, in case there are
        // a lot of messages...
        Preferences prefs = TiledConfiguration.node("io");

        if (prefs.getBoolean("reportWarnings", false)) {
            PluginLogDialog pld = new PluginLogDialog();
        }
    }
}