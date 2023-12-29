/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.selection;

import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.core.ObjectGroup;

/**
 * @author upachler
 */
public class ObjectSelection implements Selection {
    private final ObjectGroup layer;
    private final MapObject object;

    public ObjectSelection(ObjectGroup layer, MapObject o) {
        this.layer = layer;
        this.object = o;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjectSelection))
            return false;
        ObjectSelection os = (ObjectSelection) o;
        return os.getLayer() == layer && os.getObject() == object;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.layer != null ? this.layer.hashCode() : 0);
        hash = 97 * hash + (this.object != null ? this.object.hashCode() : 0);
        return hash;
    }

    public ObjectGroup getLayer() {
        return layer;
    }

    public MapObject getObject() {
        return object;
    }

}
