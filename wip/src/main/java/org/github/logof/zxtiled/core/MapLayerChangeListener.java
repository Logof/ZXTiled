/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.core;

import org.github.logof.zxtiled.core.event.MapLayerChangeEvent;

/**
 * @author upachler
 */
interface MapLayerChangeListener {
    void layerChanged(MapLayer aThis, MapLayerChangeEvent e);

}
