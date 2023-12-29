/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.undo;

import org.github.logof.zxtiled.core.Map;
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
    private final Map map;

    public MapViewportSettingsEdit(Map map) {
        backupState = new ViewportState();
        backupState.readFrom(map);
        this.map = map;
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
        s.readFrom(map);
        backupState.writeTo(map);
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

        public void readFrom(Map map) {
            viewportWidth = map.getViewportWidth();
            viewportHeight = map.getViewportHeight();
            eyeDistance = map.getEyeDistance();
        }

        public void writeTo(Map map) {
            map.setViewportWidth(viewportWidth);
            map.setViewportHeight(viewportHeight);
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
