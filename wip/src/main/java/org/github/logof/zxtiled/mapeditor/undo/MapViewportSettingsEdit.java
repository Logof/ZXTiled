/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.undo;

import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.undo.AbstractUndoableEdit;

/**
 * @author count
 */
public class MapViewportSettingsEdit extends AbstractUndoableEdit {
    private final TileMap tileMap;

    public MapViewportSettingsEdit(TileMap tileMap) {
        this.tileMap = tileMap;
    }


    @Override
    public String getPresentationName() {
        return Resources.getString("edit.change.map.viewport.name");
    }

}
