/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.selection;

import lombok.Getter;
import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.core.ObjectsLayer;

/**
 * @author upachler
 */
@Getter
public class ObjectSelection implements Selection {
    private final ObjectsLayer objectsLayer;
    private final MapObject mapObject;

    public ObjectSelection(ObjectsLayer objectsLayer, MapObject mapObject) {
        this.objectsLayer = objectsLayer;
        this.mapObject = mapObject;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObjectSelection)) {
            return false;
        }
        ObjectSelection objectSelection = (ObjectSelection) object;
        return objectSelection.getObjectsLayer() == objectsLayer && objectSelection.getMapObject() == mapObject;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.objectsLayer != null ? this.objectsLayer.hashCode() : 0);
        hash = 97 * hash + (this.mapObject != null ? this.mapObject.hashCode() : 0);
        return hash;
    }
}
