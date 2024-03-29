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

package org.github.logof.zxtiled.mapeditor.util;

import org.github.logof.zxtiled.core.Tile;
import java.util.EventObject;

/**
 * An event that describes the selection of a tile.
 *
 * @version $Id$
 */
public class TileSelectionEvent extends EventObject {
    private final Tile tile;

    public TileSelectionEvent(Object source, Tile tile) {
        super(source);
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }
}
