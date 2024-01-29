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

package org.github.logof.zxtiled.core;

import lombok.Getter;
import org.github.logof.zxtiled.core.event.MapLayerChangeEvent;
import java.awt.*;
import java.awt.geom.Area;
import java.util.Properties;
import java.util.Vector;

/**
 * A layer of a map.
 *
 * @see TileMap
 * @see MultilayerPlane
 */
public abstract class MapLayer implements Cloneable {
    /**
     * MIRROR_HORIZONTAL
     */
    public static final int MIRROR_HORIZONTAL = 1;
    /**
     * MIRROR_VERTICAL
     */
    public static final int MIRROR_VERTICAL = 2;

    @Getter
    protected String name;
    protected boolean isVisible = true;
    protected boolean bLocked = false;

    protected Rectangle bounds;
    @Getter
    private float viewPlaneDistance = 0.0f;
    @Getter
    private boolean viewPlaneInfinitelyFarAway = false;
    @Getter
    private TileMap tileMap;
    @Getter
    private Properties properties = new Properties();
    private final Vector<MapLayerChangeListener> listeners = new Vector<>();

    public MapLayer() {
        bounds = new Rectangle();
        setMap(null);
    }

    /**
     * @param width width in tiles
     * @param height height in tiles
     */
    public MapLayer(int width, int height) {
        this(new Rectangle(0, 0, width, height));
    }

    public MapLayer(Rectangle r) {
        this();
        setBounds(r);
    }

    /**
     * Creates a new MapLayer instance for the given map.
     * The width and height are set to the width and height of the map.
     *
     * @param tileMap the map this layer is part of
     */
    MapLayer(TileMap tileMap) {
        this(tileMap.getWidth(), tileMap.getHeight());
        setMap(tileMap);
    }

    /**
     * Creates a new MapLayer instance for the given map.
     *
     * @param tileMap the map this layer is part of
     * @param w   width in tiles
     * @param h   height in tiles
     */
    public MapLayer(TileMap tileMap, int w, int h) {
        this(w, h);
        setMap(tileMap);
    }

    /**
     * Performs a linear translation of this layer by (<i>dx, dy</i>).
     *
     * @param dx distance over x axis
     * @param dy distance over y axis
     */
    public void translate(int dx, int dy) {
        bounds.x += dx;
        bounds.y += dy;
    }

    public abstract void mirror(int dir);

    /**
     * Sets the map this layer is part of.
     *
     * @param tileMap the Map object
     */
    public void setMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    /**
     * Sets the offset of this map layer. The offset is a distance by which to
     * shift this layer from the origin of the map.
     *
     * @param xOff x offset in tiles
     * @param yOff y offset in tiles
     */
    public void setOffset(int xOff, int yOff) {
        bounds.x = xOff;
        bounds.y = yOff;
    }

    /**
     * Sets the name of this layer.
     *
     * @param name the new name
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        fireRenamed(oldName, name);
    }

    /**
     * Returns layer width in tiles.
     *
     * @return layer width in tiles.
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Returns layer height in tiles.
     *
     * @return layer height in tiles.
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Returns the layer bounds in tiles.
     *
     * @return the layer bounds in tiles
     */
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    /**
     * Sets the bounds (in tiles) to the specified Rectangle.
     *
     * @param bounds
     */
    protected void setBounds(Rectangle bounds) {
        this.bounds = new Rectangle(bounds);
    }

    /**
     * Assigns the layer bounds in tiles to the given rectangle.
     *
     * @param rect the rectangle to which the layer bounds are assigned
     */
    public void getBounds(Rectangle rect) {
        rect.setBounds(bounds);
    }

    /**
     * A convenience method to check if a point in tile-space is within
     * the layer boundaries.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return <code>true</code> if the point (x,y) is within the layer
     * boundaries, <code>false</code> otherwise.
     */
    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    /**
     * Returns whether this layer is visible.
     *
     * @return <code>true</code> if the layer is visible, <code>false</code>
     * otherwise.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Sets the visibility of this map layer. If it changes from its current
     * value, a MapChangedEvent is fired.
     *
     * @param visible <code>true</code> to make the layer visible;
     *                <code>false</code> to make it invisible
     */
    public void setVisible(boolean visible) {
        if (isVisible != visible) {
            isVisible = visible;
            if (getTileMap() != null) {
                getTileMap().fireMapChanged();
            }
        }
    }

    /**
     * Merges the tile data of this layer with the specified layer. The calling
     * layer is considered the significant layer, and will overwrite the data
     * of the argument layer. At cells where the calling layer has no data, the
     * argument layer data is preserved.
     *
     * @param other the insignificant layer to merge with
     */
    public abstract void mergeOnto(MapLayer other);

    public abstract void maskedMergeOnto(MapLayer other, Area mask);

    public abstract void copyFrom(MapLayer other);

    public abstract void maskedCopyFrom(MapLayer other, Area mask);

    public abstract MapLayer createDiff(MapLayer ml);

    /**
     * Unlike mergeOnto, copyTo includes the null tile when merging
     *
     * @param other the layer to copy this layer to
     * @see MapLayer#copyFrom
     * @see MapLayer#mergeOnto
     */
    public void copyTo(MapLayer other) {

        // undo/redo is using this, so it better be accurate...
        other.setName(name);
        other.setVisible(isVisible);
        other.setLocked(bLocked);
        other.setViewPlaneDistance(getViewPlaneDistance());
        other.setViewPlaneInfinitelyFarAway(isViewPlaneInfinitelyFarAway());
        other.tileMap = tileMap;
        other.bounds.setBounds(bounds);
        if (other.properties != properties) {
            other.properties.clear();
            other.properties.putAll(properties);
        }
    }

    public abstract boolean isEmpty();

    /**
     * Creates a copy of this layer.
     *
     * @return a clone of this layer, as complete as possible
     * @throws CloneNotSupportedException
     * @see Object#clone
     */
    public Object clone() throws CloneNotSupportedException {
        MapLayer clone = (MapLayer) super.clone();

        // Create a new bounds object
        clone.bounds = new Rectangle(bounds);
        clone.properties = (Properties) properties.clone();

        return clone;
    }

    /**
     * @param width  the new width of the layer
     * @param height the new height of the layer
     * @param dx     the shift in x direction
     * @param dy     the shift in y direction
     * @see MultilayerPlane#resize
     */
    public abstract void resize(int width, int height, int dx, int dy);

    /**
     * Get the locked status of the layer.
     *
     * @return whether the layer is locked
     * @see MapLayer#setLocked(boolean)
     */
    public boolean getLocked() {
        return bLocked;
    }

    /**
     * Set the locked status of the layer. A locked layer can't be edited.
     *
     * @param lock <code>true</code> to lock the layer, <code>false</code> to
     *             unlock the layer
     */
    public void setLocked(boolean lock) {
        bLocked = lock;
    }

    public void setProperties(Properties p) {
        properties.clear();
        properties.putAll(p);
    }

    public boolean cannotEdit() {
        return getLocked() || !isVisible();
    }

    /// returns the tile height that applies to this layer. To layers of this
    /// type, the map's own tile height will apply. However, subtypes may
    /// implement their own tile width and tile height settings that differ
    /// from the one that the map is using.
    public int getTileHeight() {
        return getTileMap().getTileHeight();
    }

    /// returns the tile width that applies to this layer. To layers of this
    /// type, the map's own tile width will apply. However, subtypes may
    /// implement their own tile width and tile height settings that differ
    /// from the one that the map is using.
    public int getTileWidth() {
        return getTileMap().getTileWidth();
    }

    public void setViewPlaneDistance(float viewPlaneDistance) {
        if (this.viewPlaneDistance == viewPlaneDistance) {
            return;
        }
        this.viewPlaneDistance = viewPlaneDistance;
    }

    public void setViewPlaneInfinitelyFarAway(boolean inifitelyFarAway) {
        if (inifitelyFarAway == this.viewPlaneInfinitelyFarAway) {
            return;
        }
        this.viewPlaneInfinitelyFarAway = inifitelyFarAway;
    }

    private void fireRenamed(String newName, String oldName) {
        MapLayerChangeEvent e = MapLayerChangeEvent.createNameChangeEvent(oldName, newName);
        for (MapLayerChangeListener l : listeners)
            l.layerChanged(this, e);
    }

    void addMapLayerChangeListener(MapLayerChangeListener l) {
        listeners.add(l);
    }

    void removeMapLayerChangeListener(MapLayerChangeListener l) {
        listeners.remove(l);
    }
}
