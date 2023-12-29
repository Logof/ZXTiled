/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.core;

import org.github.logof.zxtiled.core.event.MapChangedEvent;
import org.github.logof.zxtiled.core.event.MapLayerChangeEvent;

/**
 * @author upachler
 */
public class MapChangeAdapter implements MapChangeListener {

    public void mapChanged(MapChangedEvent e) {
    }

    public void layerAdded(MapChangedEvent e) {
    }

    public void layerRemoved(MapChangedEvent e) {
    }

    public void layerMoved(MapChangedEvent e) {
    }

    public void tilesetAdded(MapChangedEvent e, TileSet tileset) {
    }

    public void tilesetRemoved(MapChangedEvent e, int index) {
    }

    public void tilesetsSwapped(MapChangedEvent e, int index0, int index1) {
    }

    public void layerChanged(MapChangedEvent e, MapLayerChangeEvent mlce) {
    }
}
