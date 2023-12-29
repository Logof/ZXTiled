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

package org.github.logof.zxtiled.core.event;

import lombok.Getter;
import org.github.logof.zxtiled.core.Map;
import java.util.EventObject;

/**
 * @version $Id$
 */
@Getter
public class MapChangedEvent extends EventObject {
    private int layerIndex;
    private final int oldLayerIndex;

    public MapChangedEvent(Map map) {
        this(map, -1);
        layerIndex = -1;
    }

    public MapChangedEvent(Map map, int layerIndex) {
        this(map, layerIndex, -1);
    }

    public MapChangedEvent(Map map, int layerIndex, int oldLayerIndex) {
        super(map);
        this.layerIndex = layerIndex;
        this.oldLayerIndex = oldLayerIndex;
    }

    public Map getMap() {
        return (Map) getSource();
    }
}
