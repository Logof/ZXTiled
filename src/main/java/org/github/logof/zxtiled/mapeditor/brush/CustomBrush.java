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

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.MultilayerPlane;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.view.MapView;
import java.awt.*;
import java.util.ListIterator;

public class CustomBrush extends AbstractBrush {
    public CustomBrush(MultilayerPlane mlp) {
        addAllLayers(mlp.getLayerVector());
        fitBoundsToLayers();
    }

    public CustomBrush(TileLayer tileLayer) {
        addLayer(tileLayer);
        fitBoundsToLayers();
    }

    public int getAffectedLayers() {
        return getTotalLayers();
    }

    public void setAffectedLayers(int num) {
    }

    public Shape getShape() {
        return getBounds();
    }

    /**
     * Determines whether this brush is equal to another brush.
     */
    public boolean equals(Brush b) {
        if (b instanceof CustomBrush) {
            if (b == this) return true;
            else {
                //TODO: THIS
            }
        }
        return false;
    }

    public void startPaint(MultilayerPlane mp, int x, int y, int button, int layer) {
        super.startPaint(mp, x, y, button, layer);
    }

    /**
     * The custom brush will merge its internal layers onto the layers of the
     * specified MultilayerPlane.
     *
     * @throws Exception
     * @see TileLayer#mergeOnto(MapLayer)
     * @see org.github.logof.zxtiled.mapeditor.brush.Brush#doPaint(int, int)
     */
    public Rectangle doPaint(int x, int y) throws Exception {
        int layer = initLayer;
        int centerx = x - bounds.width / 2;
        int centery = y - bounds.height / 2;

        super.doPaint(x, y);

        ListIterator<MapLayer> itr = getLayers();
        while (itr.hasNext()) {
            TileLayer tl = (TileLayer) itr.next();
            TileLayer tm = (TileLayer) affectedMp.getLayer(layer++);
            if (tm != null && tm.isVisible()) {
                tl.setOffset(centerx, centery);
                tl.mergeOnto(tm);
            }
        }

        return new Rectangle(centerx, centery, bounds.width, bounds.height);
    }

    public void drawPreview(Graphics2D g2d, MapView mv) {
        mv.paintSubMap(this, g2d);
    }
}
