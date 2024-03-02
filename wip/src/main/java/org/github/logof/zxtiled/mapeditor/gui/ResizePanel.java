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

package org.github.logof.zxtiled.mapeditor.gui;

import lombok.Getter;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.view.MapView;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * A special widget designed as an aid for resizing the map. Based on a similar
 * widget used by the GIMP when resizing the image.
 *
 * @version $Id$
 */
public class ResizePanel extends JPanel {
    private MapView inner;
    private TileMap currentTileMap;
    private Dimension oldDim;
    private Dimension newDim;
    private int offsetX, offsetY;
    private Point startPress;
    @Getter
    private double zoom;

    public ResizePanel() {
        setLayout(new OverlayLayout(this));
        setBorder(BorderFactory.createLoweredBevelBorder());
    }

    public ResizePanel(TileMap tileMap) {
        this();
        zoom = 0.1;
        currentTileMap = tileMap;

        DragHandler dragHandler = new DragHandler();

        inner = MapView.createViewforMap(tileMap);
        assert inner != null;
        inner.setZoom(zoom);
        inner.addMouseListener(dragHandler);
        inner.addMouseMotionListener(dragHandler);
        add(inner);

        Dimension old = inner.getPreferredSize();
        // TODO: get smaller dimension, zoom based on that...
        oldDim = old;
        setSize(old);
    }

    public ResizePanel(Dimension size, TileMap tileMap) {
        this(tileMap);
        oldDim = size;
        newDim = size;
        setSize(size);
    }

    public void moveMap(int x, int y) {
        // snap!
        inner.setLocation(
                (int) (x * (Constants.TILE_WIDTH * zoom)),
                (int) (y * (Constants.TILE_HEIGHT * zoom)));
    }

    public void setNewDimensions(Dimension n) {
        newDim = n;
        // TODO: recalc the map size...
    }

    public Dimension getPreferredSize() {
        return oldDim;
    }

    private class DragHandler extends MouseInputAdapter {
        public void mousePressed(MouseEvent e) {
            startPress = e.getPoint();
        }

        public void mouseReleased(MouseEvent e) {
            startPress = null;
        }

        public void mouseDragged(MouseEvent e) {
            int newOffsetX = offsetX + (e.getX() - startPress.x);
            int newOffsetY = offsetY + (e.getY() - startPress.y);

            newOffsetX /= (int) (Constants.TILE_WIDTH * zoom);
            newOffsetY /= (int) (Constants.TILE_HEIGHT * zoom);

            if (newOffsetX != offsetX) {
                firePropertyChange("offsetX", offsetX, newOffsetX);
                offsetX = newOffsetX;
            }

            if (newOffsetY != offsetY) {
                firePropertyChange("offsetY", offsetY, newOffsetY);
                offsetY = newOffsetY;
            }
        }
    }
}
