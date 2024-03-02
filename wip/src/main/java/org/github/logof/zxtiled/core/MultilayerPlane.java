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
import lombok.Setter;
import java.awt.*;
import java.util.Vector;

/**
 * MultilayerPlane makes up the core functionality of both Maps and Brushes.
 * This class handles the order of layers as a group.
 */
@Getter
@Setter
public class MultilayerPlane {
    protected Rectangle bounds;
    private TileLayer tileLayer;
    private ObjectLayer objectLayer;

    /**
     * Default constructor.
     */
    public MultilayerPlane() {
        tileLayer = new TileLayer();
        objectLayer = new ObjectLayer();
        bounds = new Rectangle();
    }

    /**
     * Construct a MultilayerPlane to the specified dimensions.
     *
     * @param width  - Count of screens horizontally
     * @param height - Count of screens vertically
     */
    public MultilayerPlane(int width, int height) {
        this();
        bounds.width = width;
        bounds.height = height;
    }

    /**
     * Returns a <code>Rectangle</code> representing the maximum bounds in tiles.
     *
     * @return a new rectangle containing the maximum bounds of this plane
     */
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    /**
     * Adds a layer to the map.
     *
     * @param layer The {@link MapLayer} to add
     */
    public void addLayer(MapLayer layer) {
        //layers.add(layers.size(), layer);
        System.out.println("Добавление слоев не поддерживается");
    }

    public void setLayer(int index, MapLayer layer) {
        //layers.set(index, layer);
        System.out.println("Изменение слоев не поддерживается");
    }

    /**
     * Removes the layer at the specified index. Layers above this layer will
     * move down to fill the gap.
     *
     * @param index the index of the layer to be removed
     * @return the layer that was removed from the list
     */
    public MapLayer removeLayer(int index) {
        //return layers.remove(index);
        System.out.println("Удаление слоев не поддерживается");
        return null;
    }

    /**
     * Removes all layers from the plane.
     */
    public void removeAllLayers() {
        // layers.removeAllElements();
        System.out.println("Удаление всех слоев не поддерживается");
    }

    /**
     * Returns the layer vector.
     *
     * @return Vector the layer vector
     */
    public Vector<MapLayer> getLayerVector() {
        //return layers;
        System.out.println("Получение списка слоев не поддерживается");
        return new Vector<>();
    }

    /**
     * Sets the layer vector to the given java.util.Vector.
     *
     * @param layers the new set of layers
     */
    public void setLayerVector(Vector<MapLayer> layers) {
        //this.layers = layers;
        System.out.println("Изменение списка слоев не поддерживается");
    }

    /**
     * Finds the index of the given MapLayer instance. If the given layer is
     * not part of this Map, the function returns -1;
     *
     * @param mapLayer the layer to request the index of.
     * @return the layer index or -1 if the layer could not be found
     */
    protected int findLayerIndex(MapLayer mapLayer) {
        //return layers.indexOf(mapLayer);
        System.out.println("Поиск в списке слоев не поддерживается");
        return -1;
    }

    /**
     * Returns the layer at the specified vector index.
     *
     * @param i the index of the layer to return
     * @return the layer at the specified index, or null if the index is out of
     * bounds
     */
    //TODO требует рефакторинга, чтобы обращаться нужно не по Id, а, например, по типу слоя
    public MapLayer getLayer(int i) {
        if (i == 1) {
            return objectLayer;
        }
        return tileLayer;
        //return layers.get(i);
    }

    /**
     * Resizes this plane. The (dx, dy) pair determines where the original
     * plane should be positioned on the new area. Only layers that exactly
     * match the bounds of the map are resized, any other layers are moved by
     * the given shift.
     * This method will resize all layers first (if there are any) and then
     * call <code>resize(width,height)</code>
     *
     * @param width  The new width of the map.
     * @param height The new height of the map.
     * @param dx     The shift in x direction in tiles.
     * @param dy     The shift in y direction in tiles.
     * @see MapLayer#resize
     */
    public void resize(int width, int height, int dx, int dy) {
        tileLayer.resize(width, height, dx, dy);
        objectLayer.resize(width, height, dx, dy);
        resize(width, height);
    }

    /**
     * Resizes this plane. The plane's layers will not be affected.
     *
     * @param width  The plane's new width
     * @param height The plane's new height
     * @see MapLayer#resize
     */
    public void resize(int width, int height) {
        bounds.width = width;
        bounds.height = height;
    }

    /**
     * Determines whether the point (x,y) falls within the plane.
     *
     * @param x coordinate by X
     * @param y coordinate by Y
     * @return <code>true</code> if the point is within the plane,
     * <code>false</code> otherwise
     */
    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < bounds.width && y < bounds.height;
    }
}
