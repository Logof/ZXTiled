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

package org.github.logof.zxtiled.mapeditor.brush;

import org.github.logof.zxtiled.core.MultilayerPlane;
import org.github.logof.zxtiled.view.MapView;
import java.awt.*;


public interface Brush {

    /**
     * Returns the bounds of this brush. This is used for determining the area
     * to redraw when the brush moves.
     */
    Rectangle getBounds();

    /**
     * Called before painting operation starts. This is when the mouse is
     * initially pressed.
     *
     * @param mp     the MultilayerPlane to be affected.
     * @param x      the tile x-coordinate where the user initiated the paint.
     * @param y      the tile y-coordinate where the user initiated the paint.
     * @param button the mouse button that was used.
     * @param layer  the selected layer.
     * @see MultilayerPlane
     */
    void startPaint(MultilayerPlane mp, int x, int y, int button, int layer);

    /**
     * This is the main processing method for a brush. This method should only
     * be called between calls to startPaint and endPaint.
     *
     * @param x the tile x-coordinate of the mouse.
     * @param y the tile y-coordinate of the mouse.
     * @return the rectangular region affected by the painting, used to
     * determine which area to redraw.
     * @throws Exception
     */
    Rectangle doPaint(int x, int y) throws Exception;

    /**
     * Called when painting operation finishes. This is when the mouse is
     * released.
     */
    void endPaint();

    /**
     * Draws a preview of the editing operation when applicable.
     *
     * @param g2d The graphics context to draw to.
     * @param mv
     */
    void drawPreview(Graphics2D g2d, MapView mv);

    /**
     * Draws a preview of the editing operation when applicable. This is meant
     * for off-map brush preview. The map view is provided for drawing map-view
     * dependant previews.
     *
     * @param g2d       The graphics context to draw to.
     * @param dimension The dimension within which the preview should be drawn
     * @param mv        The active map view.
     */
    void drawPreview(Graphics2D g2d, Dimension dimension, MapView mv);

    /**
     * Returns wether this brush equals another brush.
     *
     * @param brush
     * @return boolean
     */
    boolean equals(Brush brush);
}
