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

import org.github.logof.zxtiled.core.Map;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;

/**
 * Swaps the currently selected layer with the layer below.
 *
 * @version $Id$
 */
public class MoveLayerDownAction extends AbstractLayerAction {
    public MoveLayerDownAction(MapEditor editor) {
        super(editor,
                Resources.getString("action.layer.movedown.name"),
                Resources.getString("action.layer.movedown.tooltip"),
                Resources.getIcon("icon/gnome-down.png"));

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift PAGE_DOWN"));
    }

    protected void doPerformAction() {
        Map map = editor.getCurrentMap();
        int layerIndex = editor.getCurrentLayerIndex();

        if (layerIndex > 0) {
            map.swapLayerDown(layerIndex);
            editor.setCurrentLayerIndex(layerIndex - 1);
        }
    }
}
