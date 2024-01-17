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
import org.github.logof.zxtiled.mapeditor.undo.DeleteLayerEdit;
import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import java.awt.event.ActionEvent;

/**
 * Deletes the selected layer and selects the layer that takes the same index.
 *
 * @version $Id$
 */
public class DeleteLayerAction extends AbstractAction {
    MapEditor editor;

    public DeleteLayerAction(MapEditor editor) {
        super(Resources.getString("action.layer.delete.name"),
                Resources.getIcon("icon/gnome-delete.png"));
        this.editor = editor;
        putValue(SHORT_DESCRIPTION, "action.layer.delete.name");
    }

    public void actionPerformed(ActionEvent e) {
        TileMap tileMap = editor.getCurrentTileMap();
        int layerIndex = editor.getCurrentLayerIndex();
        int totalLayers = tileMap.getTotalLayers();

        UndoableEdit layerDeleteEdit = new DeleteLayerEdit(editor, tileMap, layerIndex);

        if (layerIndex >= 0) {
            tileMap.removeLayer(layerIndex);

            // If the topmost layer was selected, the layer index is invalid
            // after removing that layer. The right thing to do is to reset it
            // to the new topmost layer.
            if (layerIndex == totalLayers - 1) {
                editor.setCurrentLayerIndex(totalLayers - 2);
            }
        }

        editor.getUndoSupport().postEdit(layerDeleteEdit);
    }
}
