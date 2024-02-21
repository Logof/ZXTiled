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

package org.github.logof.zxtiled.mapeditor.undo;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.TileMap;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Vector;

/**
 * A change in the layer state. Used for adding, removing and rearranging
 * the layer stack of a map.
 */
public class MapLayerStateEdit extends AbstractUndoableEdit {
    private final TileMap tileMap;
    private final Vector<MapLayer> layersBefore;
    private final Vector<MapLayer> layersAfter;
    private final String name;

    public MapLayerStateEdit(TileMap m,
                             Vector<MapLayer> before,
                             Vector<MapLayer> after,
                             String name) {
        tileMap = m;
        layersBefore = before;
        layersAfter = after;
        this.name = name;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        tileMap.setLayerVector(layersBefore);
    }

    public void redo() throws CannotRedoException {
        super.redo();
        tileMap.setLayerVector(layersAfter);
    }

    public String getPresentationName() {
        return name;
    }
}
