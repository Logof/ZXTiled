/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.undo;

import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.undo.AbstractUndoableEdit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author count
 */
public class MapViewportSettingsEdit extends AbstractUndoableEdit {
    private ViewportState backupState;
    private boolean undone = false;
    private final TileMap tileMap;

    public MapViewportSettingsEdit(TileMap tileMap) {
        backupState = new ViewportState();
        backupState.readFrom(tileMap);
        this.tileMap = tileMap;
    }

    public void undo() {
        super.undo();
        assert !undone;
        swapViewportState();
        undone = true;
    }

    public void redo() {
        super.redo();
        assert undone;
        swapViewportState();
        undone = false;
    }

    private void swapViewportState() {
        ViewportState s = backupState.duplicate();
        s.readFrom(tileMap);
        backupState.writeTo(tileMap);
        backupState = s;
    }

    @Override
    public String getPresentationName() {
        return Resources.getString("edit.change.map.viewport.name");
    }

    private static class ViewportState implements Cloneable {
        public int viewportWidth;
        public int viewportHeight;
        public float eyeDistance;

        public void readFrom(TileMap tileMap) {
            viewportWidth = tileMap.getViewportWidth();
            viewportHeight = tileMap.getViewportHeight();
            eyeDistance = tileMap.getEyeDistance();
        }

        public void writeTo(TileMap tileMap) {
            tileMap.setViewportWidth(viewportWidth);
            tileMap.setViewportHeight(viewportHeight);
        }

        public ViewportState duplicate() {
            try {
                return (ViewportState) clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(MapViewportSettingsEdit.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

}
