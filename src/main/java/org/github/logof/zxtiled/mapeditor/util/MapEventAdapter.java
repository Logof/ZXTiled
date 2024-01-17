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

package org.github.logof.zxtiled.mapeditor.util;

import java.awt.*;
import java.util.LinkedList;

public class MapEventAdapter {
    public static final int MAP_EVENT_MAP_ACTIVE = 1;
    public static final int MAP_EVENT_MAP_INACTIVE = 2;

    private static final LinkedList<Component> listeners = new LinkedList<>();

    /**
     * Adds a Component to the list of listeners of map events. Checks that
     * the component is not already in the list.
     *
     * @param obj the listener to add
     */
    public static void addListener(Component obj) {
        /* Small sanity check - don't add it if it's already there.
         * Really only useful to the removeListener() func, as
         * LinkedList.remove() only removes the first instance of a given
         * object.
         */
        if (!listeners.contains(obj)) {
            listeners.add(obj);
        }
    }

    /**
     * Removes a component from the list of listeners.
     *
     * @param obj the Component to remove
     */
    public static void removeListener(Component obj) {
        listeners.remove(obj);
    }

    /**
     * Fires an event to notify all listeners.
     *
     * @param type the event type
     */
    public static void fireEvent(int type) {
        //TODO: the idea is to extend this to allow for a multitude of different event types at some point...
        switch (type) {
            case MAP_EVENT_MAP_ACTIVE:
                enableEvent();
                break;
            case MAP_EVENT_MAP_INACTIVE:
                disableEvent();
                break;
        }
    }

    private static void enableEvent() {
        for (Component listener : listeners) {
            listener.setEnabled(true);
        }
    }

    private static void disableEvent() {
        for (Component listener : listeners) {
            listener.setEnabled(false);
        }
    }
}
