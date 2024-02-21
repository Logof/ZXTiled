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

package org.github.logof.zxtiled.view;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.ObjectLayer;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.objects.HotspotObject;
import org.github.logof.zxtiled.core.objects.MovingObject;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * An orthographic map view.
 */
public class SideScrolledMapView extends MapView {

    /**
     *
     * Creates a new orthographic map view that displays the specified map.
     *
     * @param tileMap the map to be displayed by this map view
     */
    public SideScrolledMapView(TileMap tileMap) {
        super(tileMap);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        Dimension tileSize = getTileSizeWithZoom();

        if (orientation == SwingConstants.VERTICAL) {
            return (visibleRect.height / tileSize.height) * tileSize.height;
        } else {
            return (visibleRect.width / tileSize.width) * tileSize.width;
        }
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation, int direction) {
        Dimension tileSize = getTileSizeWithZoom();
        if (orientation == SwingConstants.VERTICAL) {
            return tileSize.height;
        } else {
            return tileSize.width;
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(
                tileMap.getWidth() * getTileSizeWithZoom().width,
                tileMap.getHeight() * getTileSizeWithZoom().height);
    }


    protected void paintLayer(Graphics2D g2d, TileLayer layer) {
        // Determine tile size and offset
        Dimension tileSize = getTileSizeWithZoom();
        if (tileSize.width <= 0 || tileSize.height <= 0) {
            return;
        }

        Polygon gridPoly = createGridPolygon(tileSize, 0, -tileSize.height, 0);

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = g2d.getClipBounds();
        Point start = this.screenToTileCoordinates(layer, clipRect.x, clipRect.y);
        Point end = this.screenToTileCoordinates(layer, (clipRect.x + clipRect.width), (clipRect.y + clipRect.height));
        end.x += 1;
        end.y += 3;

        boolean isSelectionLayer = layer instanceof SelectionLayer;

        // Draw this map layer
        for (int y = start.y, gy = (start.y + 1) * tileSize.height; y < end.y; y++, gy += tileSize.height) {
            for (int x = start.x, gx = start.x * tileSize.width; x < end.x; x++, gx += tileSize.width) {
                Tile tile = layer.getTileAt(x, y);

                if (tile == null) {
                    continue;
                }

                if (isSelectionLayer) {
                    gridPoly.translate(gx, gy);
                    g2d.fillPolygon(gridPoly);
                    gridPoly.translate(-gx, -gy);
                } else {
                    tile.draw(g2d, gx, gy, zoom);
                }
            }
        }
    }

    //TODO подумать, как хранить в координатах тайла или в пикселях
    protected void paintObjectLayer(Graphics2D graphics2D, ObjectLayer objectLayer) {
        final Dimension tileSize = getTileSizeWithZoom();
        assert tileSize.width != 0 && tileSize.height != 0;

        final Rectangle bounds = objectLayer.getBounds();

        graphics2D.translate(bounds.x * tileSize.width, bounds.y * tileSize.height);

        for (MovingObject movingObject : objectLayer.getEnemyList()) {
            movingObject.repaint(graphics2D, zoom);
        }

        for (HotspotObject hotspotObject : objectLayer.getHotspotList()) {
            hotspotObject.repaint(graphics2D, zoom);
        }

        if (objectLayer.getPlayerStartObject() != null) {
            objectLayer.getPlayerStartObject().repaint(graphics2D, zoom);
        }

        if (objectLayer.getPlayerFinishObject() != null) {
            objectLayer.getPlayerFinishObject().repaint(graphics2D, zoom);
        }

        graphics2D.translate(
                -bounds.x * tileSize.width,
                -bounds.y * tileSize.height);
    }

    protected void paintGrid(Graphics2D g2d) {
        MapLayer currentLayer = getCurrentLayer();
        // the grid size is dependent on the current layer - no current layer, no grid.
        if (currentLayer == null) {
            return;
        }

        // Determine tile size
        Dimension tileSize = getTileSizeWithZoom();
        if (tileSize.width <= 0 || tileSize.height <= 0) {
            return;
        }

        // Determine lines to draw from clipping rectangle
        Rectangle clipRect = g2d.getClipBounds();

        // transforming coordinates back and forth between screen and tile 
        // coordinates to quantise the given screen rectangle to coordinates bla 
        // that match the grid lines
        Point startTile = screenToTileCoordinates(currentLayer, clipRect.x, clipRect.y);

        Point start = tileToScreenCoords(tileSize, startTile.x, startTile.y);
        Point end = new Point(clipRect.x + clipRect.width, clipRect.y + clipRect.height);

        for (int x = start.x; x < end.x; x += tileSize.width) {
            g2d.setColor((x % (Constants.SCREEN_WIDTH * tileSize.width) == 0)
                    ? DEFAULT_GRID_SCREEN_COLOR
                    : DEFAULT_GRID_COLOR);
            g2d.drawLine(x, clipRect.y, x, clipRect.y + clipRect.height - 1);
        }
        for (int y = start.y; y < end.y; y += tileSize.height) {
            g2d.setColor((y % (Constants.SCREEN_HEIGHT * tileSize.height) == 0)
                    ? DEFAULT_GRID_SCREEN_COLOR
                    : DEFAULT_GRID_COLOR);
            g2d.drawLine(clipRect.x, y, clipRect.x + clipRect.width - 1, y);
        }
    }

    protected void paintCoordinates(Graphics2D g2d) {
        // like the grid, the coordinates are dependent on the current layer
        // (since the tile size can be different from layer to layer
        MapLayer currentLayer = getCurrentLayer();
        if (currentLayer == null) {
            return;
        }

        Dimension tileSize = getTileSizeWithZoom();
        if (tileSize.width <= 0 || tileSize.height <= 0) {
            return;
        }
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Determine tile size and offset
        int minTileExtents = java.lang.Math.min(tileSize.width, tileSize.height);
        Font font = new Font("SansSerif", Font.PLAIN, minTileExtents / 4);
        g2d.setFont(font);
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = g2d.getClipBounds();
        Point start = screenToTileCoordinates(currentLayer, clipRect.x, clipRect.y);
        Point end = screenToTileCoordinates(currentLayer, clipRect.x + clipRect.width, clipRect.y + clipRect.height);
        end.x += 1;
        end.y += 1;

        // Draw the coordinates
        for (int y = start.y; y < end.y; y++) {
            Point g = tileToScreenCoords(tileSize, start.x, y);
            for (int x = start.x; x < end.x; x++) {
                String coordinates = String.format("(%d, %d)", x, y);
                Rectangle2D textSize =
                        font.getStringBounds(coordinates, fontRenderContext);

                int fx = g.x + (int) ((tileSize.width - textSize.getWidth()) / 2);
                int fy = g.y + (int) ((tileSize.height + textSize.getHeight()) / 2);

                g2d.drawString(coordinates, fx, fy);
                g.x += tileSize.width;
            }
        }
    }

    public void repaintRegion(MapLayer mapLayer, Rectangle region) {
        Dimension tileSize = getTileSizeWithZoom();
        if (tileSize.width <= 0 || tileSize.height <= 0) {
            return;
        }
        int maxExtraHeight = (int) (Constants.TILE_HEIGHT * zoom - tileSize.height);

        // Calculate the visible corners of the region
        Point start = tileToScreenCoords(region.x, region.y);
        Point end = tileToScreenCoords((region.x + region.width), (region.y + region.height));

        start.x -= maxExtraHeight;

        Rectangle dirty = new Rectangle(start.x, start.y, end.x - start.x, end.y - start.y);
        repaint(dirty);
    }

    public void repaintMapObject(Rectangle region) {
        Dimension tileSize = getTileSizeWithZoom();
        if (tileSize.width <= 0 || tileSize.height <= 0) {
            return;
        }
        int maxExtraHeight = (int) (Constants.TILE_HEIGHT * zoom - tileSize.height);

        // Calculate the visible corners of the region
        Point start = tileToScreenCoords(region.x, region.y);
        Point end = tileToScreenCoords((region.x + region.width), (region.y + region.height));

        start.x -= maxExtraHeight;

        Rectangle dirty = new Rectangle(start.x, start.y, end.x - start.x, end.y - start.y);
        repaint(dirty);
    }

    public Point screenToTileCoordinates(MapLayer layer, int x, int y) {
        Dimension tileSize = getTileSizeWithZoom();
        return new Point(x / tileSize.width, y / tileSize.height);
    }

    public Point tileToScreenCoords(Dimension tileDimension, int x, int y) {
        return new Point(x * tileDimension.width, y * tileDimension.height);
    }

    protected Dimension getTileSizeWithZoom() {
        return new Dimension(
                (int) (Constants.TILE_WIDTH * zoom),
                (int) (Constants.TILE_HEIGHT * zoom));
    }

    protected Polygon createGridPolygon(Dimension tileDimension, int tileX, int tileY, int border) {
        Polygon poly = new Polygon();
        poly.addPoint(tileX - border, tileY - border);
        poly.addPoint(tileX + tileDimension.width + border, tileY - border);
        poly.addPoint(tileX + tileDimension.width + border, tileY + tileDimension.height + border);
        poly.addPoint(tileX - border, tileY + tileDimension.height + border);
        return poly;
    }

}
