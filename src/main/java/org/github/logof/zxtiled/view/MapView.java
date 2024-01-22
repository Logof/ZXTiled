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

import lombok.Getter;
import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.core.MultilayerPlane;
import org.github.logof.zxtiled.core.ObjectGroup;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.brush.Brush;
import org.github.logof.zxtiled.mapeditor.selection.ObjectSelection;
import org.github.logof.zxtiled.mapeditor.selection.Selection;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import org.github.logof.zxtiled.mapeditor.selection.SelectionSet;
import org.github.logof.zxtiled.mapeditor.selection.SelectionSetListener;
import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/**
 * The base class for map views. This is meant to be extended for different
 * tile map orientations, such as orthagonal and isometric.
 *
 * @version $Id$
 */
public abstract class MapView extends JPanel implements Scrollable {
    public static final int PF_BOUNDARY_MODE = 0x02;
    public static final int PF_COORDINATES = 0x04;
    public static final int PF_NO_SPECIAL = 0x08;
    /**
     * The default grid color (gray).
     */
    public static final Color DEFAULT_GRID_COLOR = Color.GRAY;
    public static final Color DEFAULT_GRID_SCREEN_COLOR = Color.WHITE;
    private static final float SELECTION_RUBBER_BAND_OUTER_WIDTH = 3.0f;
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(64, 64, 64);
    public static int ZOOM_NORMAL_SIZE = 1;
    protected static double[] zoomLevels = {1.0, 1.5, 2.0, 3.0, 4.0};
    protected static Image propertyFlagImage;
    protected TileMap tileMap;
    protected Brush currentBrush;
    protected int modeFlags;
    @Getter
    protected double zoom = zoomLevels[ZOOM_NORMAL_SIZE];
    @Getter
    protected int zoomLevel = ZOOM_NORMAL_SIZE;

    // Grid properties
    protected boolean showGrid;
    protected boolean antialiasGrid;
    protected Color gridColor;
    protected int gridOpacity;
    @Getter
    private MapLayer currentLayer;    // the currently selected layer
    // viewport display properties

    private Rectangle selectionRubberBandRectangle;
    private MapLayer selectionRubberBandLayer;
    private SelectionSet selectionSet;

    /**
     * Creates a new <code>MapView</code> that displays the specified map.
     *
     * @param tileMap the map to be displayed by this map view
     */
    protected MapView(TileMap tileMap) {
        // Setup static bits on first invocation
        if (MapView.propertyFlagImage == null) {
            try {
                MapView.propertyFlagImage =
                        Resources.getImage("propertyflag-12.png").orElse(null);
            } catch (Exception ignored) {
            }
        }

        this.tileMap = tileMap;
        setOpaque(true);
    }

    /**
     * Creates a MapView instance that will render the map in the right
     * orientation.
     *
     * @param p the Map to create a view for
     * @return a suitable instance of a MapView for the given Map
     * @see TileMap#getOrientation()
     */
    public static MapView createViewforMap(TileMap p) {
        MapView mapView = null;

        int orientation = p.getOrientation();
        if (orientation == TileMap.MDO_ORTHOGONAL) {
            mapView = new OrthoMapView(p);
        }
        return mapView;
    }

    public void setSelectionSet(SelectionSet selectionSet) {
        this.selectionSet = selectionSet;
        SelectionSetListener l = new SelectionSetListener() {

            public void selectionAdded(SelectionSet selectionSet, Selection[] selections) {
                repaint();
            }

            public void selectionRemoved(SelectionSet selectionSet, Selection[] selections) {
                repaint();
            }

        };
        selectionSet.addSelectionListener(l);
    }

    /**
     * takes a Graphics2D context and a rectangle in screen coordinates,
     * and renders a rectangle on screen in a selection-rectangle style.
     * Used for selection rubber band and selected objects
     *
     * @param g2d
     * @param rectangle
     */
    private void paintSelectionRectangle(Graphics2D g2d, Rectangle rectangle) {
        // draw selection rectangle
        Stroke previousStroke = g2d.getStroke();
        Color previousColor = g2d.getColor();

        g2d.setComposite(AlphaComposite.SrcOver);

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.WHITE);
        g2d.draw(rectangle);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{0.0f, 3.0f, 6.0f}, 0.0f));
        g2d.setColor(Color.BLACK);
        g2d.draw(rectangle);

        g2d.setColor(previousColor);
        g2d.setStroke(previousStroke);
    }

    private Rectangle pixelToScreenCoordinates(Rectangle rect) {
        Point p0 = pixelToScreenCoordinates(rect.x, rect.y);
        Point p1 = pixelToScreenCoordinates(rect.x + rect.width, rect.y + rect.height);
        return new Rectangle(p0.x, p0.y, p1.x - p0.x, p1.y - p0.y);
    }

    private Rectangle addMarginToRectangle(Rectangle r) {
        return new Rectangle(r.x - (int) SELECTION_RUBBER_BAND_OUTER_WIDTH,
                r.y - (int) SELECTION_RUBBER_BAND_OUTER_WIDTH,
                r.width + 2 * (int) SELECTION_RUBBER_BAND_OUTER_WIDTH,
                r.height + 2 * (int) SELECTION_RUBBER_BAND_OUTER_WIDTH);
    }

    public void setSelectionRubberband(MapLayer selectedLayer, Rectangle selectionRubberband) {
        // change selection rectangle
        Rectangle previousSelectionRubberbandR = selectionRubberBandRectangle;
        MapLayer previousSelectedRubberbandL = selectionRubberBandLayer;

        selectionRubberBandRectangle = (selectionRubberband != null)
                ? new Rectangle(selectionRubberband)
                : null;

        selectionRubberBandLayer = selectedLayer;

        // issue repaints
        if (previousSelectionRubberbandR != null) {
            Rectangle rectangle = addMarginToRectangle(previousSelectionRubberbandR);
            repaint(pixelToScreenCoordinates(rectangle));
        }
        if (selectionRubberBandRectangle != null) {
            Rectangle rectangle = addMarginToRectangle(selectionRubberBandRectangle);
            repaint(pixelToScreenCoordinates(rectangle));
        }
    }

    public void toggleMode(int modeModifier) {
        modeFlags ^= modeModifier;
        revalidate();
        repaint();
    }

    public void setMode(int modeModifier, boolean value) {
        if (value) {
            modeFlags |= modeModifier;
        } else {
            modeFlags &= ~modeModifier;
        }
        revalidate();
        repaint();
    }

    public boolean getMode(int modeModifier) {
        return (modeFlags & modeModifier) != 0;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
        repaint();
    }

    public void setGridOpacity(int gridOpacity) {
        this.gridOpacity = gridOpacity;
        repaint();
    }

    public void setAntialiasGrid(boolean antialiasGrid) {
        this.antialiasGrid = antialiasGrid;
        repaint();
    }

    public boolean getShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        revalidate();
        repaint();
    }


    // Zooming

    /**
     * Sets a new brush. The brush can draw a preview of the change while
     * editing.
     *
     * @param brush the new brush
     */
    public void setBrush(Brush brush) {
        currentBrush = brush;
    }

    public boolean zoomIn() {
        if (zoomLevel < zoomLevels.length - 1) {
            setZoomLevel(zoomLevel + 1);
        }

        return zoomLevel < zoomLevels.length - 1;
    }

    public boolean zoomOut() {
        if (zoomLevel > 0) {
            setZoomLevel(zoomLevel - 1);
        }

        return zoomLevel > 0;
    }

    public void setZoom(double zoom) {
        if (zoom > 0) {
            this.zoom = zoom;
            setSize(getPreferredSize());
        }
    }


    // Scrolling

    public void setZoomLevel(int zoomLevel) {
        if (zoomLevel >= 0 && zoomLevel < zoomLevels.length) {
            this.zoomLevel = zoomLevel;
            setZoom(zoomLevels[zoomLevel]);
        }
    }

    public abstract Dimension getPreferredSize();

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public abstract int getScrollableUnitIncrement(Rectangle visibleRect,
                                                   int orientation, int direction);

    // Painting

    /**
     * Draws all the visible layers of the map. Takes several flags into
     * account when drawing, and will also draw the grid, and any 'special'
     * layers.
     *
     * @param graphics the Graphics2D object to paint to
     * @see javax.swing.JComponent#paintComponent(Graphics)
     * @see MapLayer
     * @see SelectionLayer
     */
    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics.create();

        MapLayer layer;
        Rectangle clip = g2d.getClipBounds();

        g2d.setStroke(new BasicStroke(2.0f));

        g2d.setColor(DEFAULT_BACKGROUND_COLOR);
        g2d.fillRect(clip.x, clip.y, clip.width, clip.height);

        paintSubMap(tileMap, g2d);

        if (!getMode(PF_NO_SPECIAL)) {
            Iterator<MapLayer> li = tileMap.getLayersSpecial();

            while (li.hasNext()) {
                layer = li.next();
                if (layer.isVisible()) {
                    if (layer instanceof SelectionLayer) {
                        g2d.setComposite(AlphaComposite.getInstance(
                                AlphaComposite.SRC_ATOP, 0.3f));
                        g2d.setColor(
                                ((SelectionLayer) layer).getHighlightColor());
                    }
                    paintLayer(g2d, (TileLayer) layer);
                }
            }

            // Paint Brush
            if (currentBrush != null) {
                currentBrush.drawPreview(g2d, this);
            }
        }

        // Grid color (also used for coordinates)
        g2d.setColor(gridColor);

        if (showGrid) {
            // Grid opacity
            if (gridOpacity < 255) {
                g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_ATOP,
                        (float) gridOpacity / 255.0f));
            } else {
                g2d.setComposite(AlphaComposite.SrcOver);
            }

            // Configure grid antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    antialiasGrid
                            ? RenderingHints.VALUE_ANTIALIAS_ON
                            : RenderingHints.VALUE_ANTIALIAS_OFF);

            g2d.setStroke(new BasicStroke());
            paintGrid(g2d);
        }

        if (getMode(PF_COORDINATES)) {
            g2d.setComposite(AlphaComposite.SrcOver);
            paintCoordinates(g2d);
        }

        //if (editor != null && editor.getCurrentLayer() instanceof TileLayer) {
        //    g2d.setComposite(AlphaComposite.SrcOver);
        //
        //    TileLayer tl = (TileLayer) editor.getCurrentLayer();
        //    if (tl != null && tl.isVisible()) {
        //        paintPropertyFlags(g2d, tl);
        //    }
        //}

        // render selected objects
        for (Selection selection : selectionSet) {
            if (ObjectSelection.class.isAssignableFrom(selection.getClass())) {
                ObjectSelection objectSelection = (ObjectSelection) selection;
                MapObject mapObject = objectSelection.getObject();
                Rectangle rectangle = pixelToScreenCoordinates(mapObject.getBounds());
                paintSelectionRectangle(g2d, rectangle);
            }
        }

        // if there's a selection, draw it
        if (selectionRubberBandRectangle != null) {
            // calculate rectangle to draw
            Rectangle r = selectionRubberBandRectangle;
            Point p0 = pixelToScreenCoordinates(r.x, r.y);
            Point p1 = pixelToScreenCoordinates(r.x + r.width, r.y + r.height);
            r = new Rectangle(p0.x, p0.y, p1.x - p0.x, p1.y - p0.y);
            paintSelectionRectangle(g2d, r);
        }
    }

    public void paintSubMap(MultilayerPlane m, Graphics2D g2d) {
        Iterator<MapLayer> li = m.getLayers();
        MapLayer layer;

        while (li.hasNext()) {
            layer = li.next();
            if (layer != null) {
                if (layer.isVisible()) {
                    g2d.setComposite(AlphaComposite.SrcOver);

                    if (layer instanceof TileLayer) {
                        paintLayer(g2d, (TileLayer) layer);
                    } else if (layer instanceof ObjectGroup) {
                        paintObjectGroup(g2d, (ObjectGroup) layer);
                    }
                }
            }
        }
    }

    /**
     * Draws a TileLayer. Implemented in a subclass.
     *
     * @param g2d   the graphics context to draw the layer onto
     * @param layer the TileLayer to be drawn
     */
    protected abstract void paintLayer(Graphics2D g2d, TileLayer layer);

    /**
     * Draws an ObjectGroup. Implemented in a subclass.
     *
     * @param g2d the graphics context to draw the object group onto
     * @param og  the ObjectGroup to be drawn
     */
    protected abstract void paintObjectGroup(Graphics2D g2d, ObjectGroup og);

    /**
     * Tells this view a certain region of the map needs to be repainted.
     * <p>
     * Same as calling repaint() unless implemented more efficiently in a
     * subclass.
     *
     * @param region the region that has changed in tile coordinates
     */
    public void repaintRegion(MapLayer layer, Rectangle region) {
        repaint();
    }

    /**
     * Draws the grid for the given layer.
     *
     * @param g2d the graphics context to draw the grid onto
     */
    protected abstract void paintGrid(Graphics2D g2d);

    /**
     * Draws the coordinates on each tile.
     *
     * @param g2d the graphics context to draw the coordinates onto
     */
    protected abstract void paintCoordinates(Graphics2D g2d);


    /**
     * Returns a Polygon that matches the grid around the specified <b>Map</b>.
     *
     * @param tileX
     * @param tileY
     * @param border
     * @return the created polygon
     */
    protected abstract Polygon createGridPolygon(Dimension tileDimension, int tileX, int tileY, int border);

    // Conversion functions

    public abstract Point screenToTileCoords(MapLayer layer, int x, int y);

    /**
     * Returns the pixel coordinates on the map based on the given screen
     * coordinates. The map pixel coordinates may be different in more ways
     * than the zoom level, depending on the projection the view implements.
     *
     * @param layer to calculate the coordinates for or null if
     *              the coordinate transformation should be done in the map's coordinate
     *              system.
     * @param x     x in screen coordinates
     * @param y     y in screen coordinates
     * @return the position in map pixel coordinates
     */
    public Point screenToPixelCoords(MapLayer layer, int x, int y) {
        return new Point((int) (x / zoom), (int) (y / zoom));
    }

    public Rectangle screenToPixelCoords(MapLayer layer, Rectangle r) {
        Point p0 = screenToPixelCoords(layer, r.x, r.y);
        Point p1 = screenToPixelCoords(layer, r.x + r.width, r.y + r.height);
        return new Rectangle(p0.x, p0.y, p1.x - p0.x, p1.y - p0.y);
    }

    /**
     * Returns the location on the screen of the top corner of a tile.
     * This method takes the current zoom level into account as well as
     * the layer's parallax level (if enabled). The input values are
     * expected to be scaled by the current zoom level already.
     *
     * @param x X coordinate of the tile in tile coordinates
     * @param y Y coordinate of the tile in tile coordinates
     * @return the point in screen space
     */
    public abstract Point tileToScreenCoords(Dimension zoomedTileSize, int x, int y);

    /// This method calls tileToScreenCoords(Point, Dimension, int, int) with
    /// the point returned from calculateParallaxOffsetZoomed(layer) and
    /// the dimension calculated from MapLayer.getLayerWidth()/Height()
    /// multiplied by the current zoom level.
    /// This method is final because it is simply considered a convenience
    /// method. Subclasses are advised to override the other overload instead.
    public final Point tileToScreenCoords(int x, int y) {
        Dimension zoomedTileSize = new Dimension((int) (16 * zoom), (int) (16 * zoom));
        return tileToScreenCoords(zoomedTileSize, x, y);
    }

    /**
     * Returns the screen coordinates on the map based on the given screen
     * coordinates. The map pixel coordinates may be different in more ways
     * than the zoom level, depending on the projection the view implements.
     *
     * @param x     x in pixel coordinates
     * @param y     y in pixel coordinates
     * @return the position in map pixel coordinates
     */
    public Point pixelToScreenCoordinates(int x, int y) {
        return new Point((int) (x * zoom), (int) (y * zoom));
    }

    public void setCurrentLayer(MapLayer layer) {
        if (this.currentLayer == layer)
            return;
        this.currentLayer = layer;
        // because of different tile sizes and/or parallax positions between
        // the old and the new current layer, a redraw might be required.
        if (getMode(PF_COORDINATES) || getShowGrid()) {
            repaint();
        }
    }
}
