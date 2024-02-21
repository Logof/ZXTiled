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
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.exception.BrushException;
import org.github.logof.zxtiled.exception.LayerInvisibleBrushException;
import org.github.logof.zxtiled.exception.LayerLockedBrushException;
import org.github.logof.zxtiled.view.MapView;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * @version $Id$
 */
public class ShapeBrush extends AbstractBrush {
    protected Area shape;
    protected Tile paintTile;

    public ShapeBrush() {
    }

    /**
     * Makes this brush a rectangular brush.
     *
     * @param rectangle a Rectangle to use as the shape of the brush
     */
    public void makeQuadBrush(Rectangle rectangle) {
        shape = new Area(new Rectangle2D.Double(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
        resize(rectangle.width, rectangle.height, 0, 0);
    }

    public void setSize(int size) {
        makeQuadBrush(new Rectangle(0, 0, size, size));
    }

    public Tile getTile() {
        return paintTile;
    }

    public void setTile(Tile tile) {
        paintTile = tile;
    }

    public Rectangle getBounds() {
        return shape.getBounds();
    }

    public Shape getShape() {
        return shape;
    }

    public void drawPreview(Graphics2D g2d, Dimension dimension, MapView mv) {
        g2d.fill(shape);
    }

    public void drawPreview(Graphics2D g2d, MapView mv) {
    }

    public boolean equals(Brush brush) {
        return brush instanceof ShapeBrush &&
                ((ShapeBrush) brush).shape.equals(shape);
    }

    public void startPaint(MultilayerPlane mp, int x, int y, int button, int layer) {
        super.startPaint(mp, x, y, button, layer);
    }

    /**
     * Paints the entire area of the brush with the set tile. This brush can
     * affect several layers.
     *
     * @throws Exception
     * @see org.github.logof.zxtiled.mapeditor.brush.Brush#doPaint(int, int)
     */
    public Rectangle doPaint(int x, int y) throws Exception {
        Rectangle shapeBounds = shape.getBounds();
        int centerx = x - shapeBounds.width / 2;
        int centery = y - shapeBounds.height / 2;

        // check if all layers are editable
        for (int layer = 0; layer < numLayers; layer++) {
            TileLayer tl = (TileLayer) affectedMp.getLayer(initLayer + layer);

            if (tl.cannotEdit()) {
                if (tl.getLocked()) {
                    throw new LayerLockedBrushException(tl);
                } else if (!tl.isVisible()) {
                    throw new LayerInvisibleBrushException(tl);
                } else {
                    throw new BrushException(tl);
                }
            }
        }

        super.doPaint(x, y);

        // FIXME: This loop does not take all edges into account

        for (int layer = 0; layer < numLayers; layer++) {
            TileLayer tl = (TileLayer) affectedMp.getLayer(initLayer + layer);
            if (tl != null) {
                for (int i = 0; i <= shapeBounds.height + 1; i++) {
                    for (int j = 0; j <= shapeBounds.width + 1; j++) {
                        if (shape.contains(j, i)) {
                            tl.setTileAt(j + centerx, i + centery, paintTile);
                        }
                    }
                }
            }
        }

        // Return affected area
        return new Rectangle(
                centerx, centery, shapeBounds.width, shapeBounds.height);
    }
}
