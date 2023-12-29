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
import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import java.text.MessageFormat;

/**
 * Clones the current layer, adds the clone to the map at the top of the layer
 * stack and then selects it.
 *
 * @version $Id$
 */
public class CloneLayerAction extends AbstractLayerAction {
    public CloneLayerAction(MapEditor editor) {
        super(editor,
                Resources.getString("action.layer.duplicate.name"),
                Resources.getString("action.layer.duplicate.tooltip"),
                Resources.getIcon("icon/gimp-duplicate-16.png"));
    }

    protected void doPerformAction() {
        MapLayer currentLayer = editor.getCurrentLayer();
        Map currentMap = editor.getCurrentMap();

        if (currentLayer != null) {
            try {
                MapLayer clone = (MapLayer) currentLayer.clone();
                String newName = Resources.getString(
                        "action.layer.duplicate.newlayer.name");
                clone.setName(MessageFormat.format(newName, clone.getName()));
                currentMap.addLayer(clone);
                editor.setCurrentLayerIndex(currentMap.getTotalLayers() - 1);
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
