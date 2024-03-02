/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.undo;

import lombok.Getter;
import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

/**
 * @author upachler
 */
public class MapLayerViewportSettingsEdit extends AbstractUndoableEdit {
    @Getter
    private final boolean significant;
    private ViewportState backupState;
    private boolean undone = false;
    private final MapLayer layer;

    public MapLayerViewportSettingsEdit(MapLayer layer, boolean significant) {
        backupState = new ViewportState();
        backupState.readFrom(layer);
        this.layer = layer;
        this.significant = significant;
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        assert !undone;

        if (!anEdit.getClass().equals(getClass())) {
            return super.addEdit(anEdit);
        }

        MapLayerViewportSettingsEdit other = (MapLayerViewportSettingsEdit) anEdit;

        // edits of different layers can't be merged
        if (layer != other.layer) {
            return false;
        }

        // edits with the same state are merged
        if (other.backupState.equals(backupState)) {
            return true;
        }

        // inisignificant changes are merged
        return !other.isSignificant();
    }

    @Override
    public String getPresentationName() {
        return Resources.getString("edit.change.layer.viewport.name");
    }

    private static class ViewportState implements Cloneable {
        public float viewPlaneDistance;
        public boolean viewPlaneInfinitelyFarAway;

        public void readFrom(MapLayer map) {
            viewPlaneDistance = map.getViewPlaneDistance();
            viewPlaneInfinitelyFarAway = map.isViewPlaneInfinitelyFarAway();
        }

        public void writeTo(MapLayer map) {
            map.setViewPlaneDistance(viewPlaneDistance);
            map.setViewPlaneInfinitelyFarAway(viewPlaneInfinitelyFarAway);
        }

        public ViewportState duplicate() {
            try {
                return (ViewportState) clone();
            } catch (CloneNotSupportedException ex) {
                return null;
            }
        }

        public boolean equals(Object o) {
            try {
                ViewportState rhs = (ViewportState) o;
                return viewPlaneDistance == rhs.viewPlaneDistance
                        && viewPlaneInfinitelyFarAway == rhs.viewPlaneInfinitelyFarAway;
            } catch (ClassCastException ccx) {
                return false;
            }

        }
    }

}
