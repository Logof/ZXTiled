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
import java.io.FileFilter;
import java.io.InputStream;


/**
 * Used by Tiled to denote a plugin for reading maps. The map file
 * can have any format, as long as the MapReader implementor returns
 * instances of {@link TileMap} and {@link Tileset}.
 */
public interface MapReader extends PluggableMapIO,
                                   FileFilter {
    /**
     * Loads a map from a file.
     *
     * @param filename the filename of the map file
     * @return A {@link TileMap} instance with the relevant data
     * @throws Exception
     */
    TileMap readMap(String filename) throws Exception;

    /**
     * Loads a tileset from a file.
     *
     * @param filename the filename of the tileset file
     * @return A {@link Tileset} instance with the relevant data
     * @throws Exception
     */
    Tileset readTileset(String filename) throws Exception;

    /**
     * Overload this to load a map from an already opened stream. Useful
     * for maps which are part of a larger binary dataset
     *
     * @param in
     * @return A {@link TileMap} object with the relevant data
     * @throws Exception
     */
    TileMap readMap(InputStream in) throws Exception;

    /**
     * Overload this to load a tileset from an open stream.
     *
     * @param in
     * @return A (@link TileSet} instance
     * @throws Exception
     */
    Tileset readTileset(InputStream in) throws Exception;
}
