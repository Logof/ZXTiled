/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.core.event;

import lombok.Getter;

/**
 * A change event for a layer specifies what change happened to that layer.
 * To know the type of change, call getChangeType(). Depending on the value
 * it returns, certain member functions will yield valid values (look at
 * the documentation for the <code>CHANGETYPE_*</code> constants for details).
 *
 * @author upachler
 */
@Getter
public class MapLayerChangeEvent {

    /**
     * Indicates that the layer in question has been renamed. The
     * getOldName() and getNewName() member functions will yield the layer's
     * old and new name.
     */
    public static final int CHANGE_TYPE_NAME = 1;

    private int changeType = -1;

    private String oldName;

    private String newName;

    private MapLayerChangeEvent(int changeType) {
        this.changeType = changeType;
    }

    public static MapLayerChangeEvent createNameChangeEvent(String oldName, String newName) {
        MapLayerChangeEvent e = new MapLayerChangeEvent(CHANGE_TYPE_NAME);
        e.oldName = oldName;
        e.newName = newName;
        return e;
    }
}
