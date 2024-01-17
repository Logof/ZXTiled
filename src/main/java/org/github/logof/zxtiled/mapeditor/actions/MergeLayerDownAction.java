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

package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;

/**
 * Merges the current layer with the one below and selects the merged layer.
 *
 * @version $Id$
 */
public class MergeLayerDownAction extends AbstractLayerAction {
    public MergeLayerDownAction(MapEditor editor) {
        super(editor,
                Resources.getString("action.layer.mergedown.name"),
                Resources.getString("action.layer.mergedown.tooltip"));

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift control M"));
    }

    protected void doPerformAction() {
        TileMap tileMap = editor.getCurrentTileMap();
        int layerIndex = editor.getCurrentLayerIndex();

        if (layerIndex > 0) {
            tileMap.mergeLayerDown(layerIndex);
            editor.setCurrentLayerIndex(layerIndex - 1);
        }
    }
}
