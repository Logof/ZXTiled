/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.brush;

import org.github.logof.zxtiled.core.MapLayer;

/**
 * @author upachler
 */
public class BrushException extends Exception {
    private final MapLayer layer;

    public BrushException(MapLayer layer) {
        this.layer = layer;
    }

    public MapLayer getLayer() {
        return layer;
    }
}
