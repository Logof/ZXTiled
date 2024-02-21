/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.exception;

import org.github.logof.zxtiled.core.MapLayer;

/**
 * @author upachler
 */
public class LayerInvisibleBrushException extends BrushException {

    public LayerInvisibleBrushException(MapLayer ml) {
        super(ml);
    }

}
