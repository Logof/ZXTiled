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

/**
 * Adds a layer to the current map and selects it.
 *
 * @version $Id$
 */
public class AddLayerAction extends AbstractLayerAction {
    public AddLayerAction(MapEditor editor) {
        super(editor,
                Resources.getString("action.layer.add.name"),
                Resources.getString("action.layer.add.tooltip"),
                Resources.getIcon("icon/gnome-new.png"));
    }

    protected void doPerformAction() {
        TileMap currentTileMap = editor.getCurrentTileMap();
        currentTileMap.addLayer();
        editor.setCurrentLayerIndex(currentTileMap.getTotalLayers() - 1);
    }
}
