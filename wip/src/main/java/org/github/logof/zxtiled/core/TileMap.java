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
import org.github.logof.zxtiled.core.event.MapChangedEvent;
import org.github.logof.zxtiled.core.event.MapLayerChangeEvent;
import org.github.logof.zxtiled.exception.LayerLockedException;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.Resources;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

/**
 * The Map class is the focal point of the <code>tiled.core</code> package.
 * This class also handles notifing listeners if there is a change to any layer
 * or object contained by the map.
 *
 * @version $Id$
 */
public class TileMap extends MultilayerPlane implements MapLayerChangeListener {
    private final Vector<MapLayer> specialLayers;
    @Getter
    @Setter
    private TileLayer tileLayer;
    @Getter
    @Setter
    private ObjectLayer objectLayer;
    
    @Getter
    private final Vector<Tileset> tilesets;
    private final List<MapChangeListener> mapChangeListeners = new LinkedList<>();

    @Setter
    @Getter
    private MapTypeEnum mapType = MapTypeEnum.MAP_SIDE_SCROLLED;

    @Setter
    @Getter
    private Properties properties;
    @Getter
    @Setter
    private String filename;

    /**
     * @param width  the map width in tiles.
     * @param height the map height in tiles.
     */
    public TileMap(int width, int height) {
        super(width, height);
        properties = new Properties();
        tilesets = new Vector<>();
        specialLayers = new Vector<>();
        tileLayer = new TileLayer();
        objectLayer = new ObjectLayer();
    }

    /**
     * Adds a change listener. The listener will be notified when the map
     * changes in certain ways.
     *
     * @param listener the change listener to add
     * @see MapChangeListener#mapChanged(MapChangedEvent)
     */
    public void addMapChangeListener(MapChangeListener listener) {
        mapChangeListeners.add(listener);
    }

    /**
     * Removes a change listener.
     *
     * @param listener the listener to remove
     */
    public void removeMapChangeListener(MapChangeListener listener) {
        mapChangeListeners.remove(listener);
    }

    /**
     * Notifies all registered map change listeners about a change.
     */
    protected void fireMapChanged() {
        MapChangedEvent event = null;
        // clone mapChangeListeners first, because otherwise we'll get
        // concurrent modification exceptions if a listener calls something
        // that add or removes listeners
        Iterable<MapChangeListener> mapChangeListenersClone = new Vector<>(mapChangeListeners);

        for (MapChangeListener mapChangeListener : mapChangeListenersClone) {
            if (event == null) {
                event = new MapChangedEvent(this);
            }
            mapChangeListener.mapChanged(event);
        }
    }

    protected void fireLayerRemoved(int layerIndex) {
        MapChangedEvent event = new MapChangedEvent(this, layerIndex);
        for (MapChangeListener listener : mapChangeListeners) {
            listener.layerRemoved(event);
        }
    }

    protected void fireLayerAdded(int layerIndex) {
        MapChangedEvent event = new MapChangedEvent(this, layerIndex);
        for (MapChangeListener listener : mapChangeListeners) {
            listener.layerAdded(event);
        }
    }

    protected void fireLayerMoved(int oldLayerIndex, int newLayerIndex) {
        MapChangedEvent event = new MapChangedEvent(this, newLayerIndex, oldLayerIndex);
        for (MapChangeListener listener : mapChangeListeners) {
            listener.layerMoved(event);
        }
    }

    protected void fireLayerChanged(int layerIndex, MapLayerChangeEvent mlce) {
        MapChangedEvent event = new MapChangedEvent(this, layerIndex);
        for (MapChangeListener listener : mapChangeListeners)
            listener.layerChanged(event, mlce);
    }

    /**
     * Notifies all registered map change listeners about the removal of a
     * tileset.
     *
     * @param index the index of the removed tileset
     */
    protected void fireTilesetRemoved(int index) {
        Iterator<MapChangeListener> iterator = mapChangeListeners.iterator();
        MapChangedEvent event = null;

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
            }
            iterator.next().tilesetRemoved(event, index);
        }
    }

    /**
     * Notifies all registered map change listeners about the addition of a
     * tileset.
     *
     * @param tileset the new tileset
     */
    protected void fireTilesetAdded(Tileset tileset) {
        Iterator<MapChangeListener> iterator = mapChangeListeners.iterator();
        MapChangedEvent event = null;

        while (iterator.hasNext()) {
            if (Objects.isNull(event)) {
                event = new MapChangedEvent(this);
            }
            iterator.next().tilesetAdded(event, tileset);
        }
    }

    /**
     * Notifies all registered map change listeners about the reorder of the
     * tilesets.
     */
    protected void fireTilesetsSwapped(int index0, int index1) {
        Iterator<MapChangeListener> iterator = mapChangeListeners.iterator();
        MapChangedEvent event = null;

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
            }
            iterator.next().tilesetsSwapped(event, index0, index1);
        }
    }


    public void addLayerSpecial(MapLayer layer) {
        layer.setMap(this);
        specialLayers.add(layer);
        fireMapChanged();
    }

    @Override
    public MapLayer addLayer(MapLayer layer) {
        layer.setMap(this);
        super.addLayer(layer);
        layer.addMapLayerChangeListener(this);
        fireMapChanged();
        fireLayerAdded(getLayerVector().indexOf(layer));
        return layer;
    }

    public void addAllLayers() {
        MapLayer layer = new TileLayer(this, bounds.width, bounds.height);
        layer.setName(Resources.getString("general.layer.layer") + " " + super.getTotalLayers());
        insertLayer(getTotalLayers(), layer);

        ObjectLayer objectLayer = new ObjectLayer(this);
        objectLayer.setName(Resources.getString("general.object.object") + " " + super.getTotalLayers());
        insertLayer(getTotalLayers(), objectLayer);
    }

    public void insertLayer(int index, MapLayer layer) {
        super.insertLayer(index, layer);
        layer.addMapLayerChangeListener(this);
        fireMapChanged();
        fireLayerAdded(index);
    }

    public void setLayer(int index, MapLayer layer) {
        layer.setMap(this);
        super.setLayer(index, layer);
        fireMapChanged();
        fireLayerRemoved(index);
        fireLayerAdded(index);
    }

    /**
     * Adds a Tileset to this Map. If the set is already attached to this map,
     * <code>addTileset</code> simply returns.
     *
     * @param tileset a tileset to add
     */
    public void addTileset(Tileset tileset) {
        if (tileset == null || tilesets.contains(tileset)) {
            return;
        }
        tilesets.add(tileset);
        fireTilesetAdded(tileset);
    }

    /**
     * Removes a {@link Tileset} from the map, and removes any tiles in the set
     * from the map layers. A {@link MapChangedEvent} is fired when all
     * processing is complete.
     *
     * @param tileset TileSet to remove
     * @throws LayerLockedException when the tileset is in use on a locked
     *                              layer
     */
    public void removeTileset(Tileset tileset) throws LayerLockedException {
        // Sanity check
        final int tilesetIndex = tilesets.indexOf(tileset);
        if (tilesetIndex == -1) {
            return;
        }

        // Go through the map and remove any instances of the tiles in the set
        Iterator<Tile> tileIterator = tileset.iterator();
        while (tileIterator.hasNext()) {
            Tile tile = tileIterator.next();
            Iterator<MapLayer> layerIterator = getLayers();
            while (layerIterator.hasNext()) {
                MapLayer mapLayer = layerIterator.next();
                if (mapLayer instanceof TileLayer) {
                    ((TileLayer) mapLayer).removeTile(tile);
                }
            }
        }

        tilesets.remove(tileset);
        fireTilesetRemoved(tilesetIndex);
    }

    /**
     * Calls super method, and additionally fires a {@link MapChangedEvent}.
     *
     * @see MultilayerPlane#removeLayer(int)
     */
    public MapLayer removeLayer(int index) {
        MapLayer layer = super.removeLayer(index);
        layer.removeMapLayerChangeListener(this);
        fireMapChanged();
        fireLayerRemoved(index);
        return layer;
    }

    public void removeLayerSpecial(MapLayer layer) {
        if (specialLayers.remove(layer)) {
            fireMapChanged();
        }
    }

    /**
     * Calls super method, and additionally fires a {@link MapChangedEvent}.
     *
     * @see MultilayerPlane#removeAllLayers
     */
    public void removeAllLayers() {
        while (getTotalLayers() > 0) {
            getLayer(0).removeMapLayerChangeListener(this);
            removeLayer(0);
            fireLayerRemoved(0);
        }
    }

    /**
     * Calls super method, and additionally fires a {@link MapChangedEvent}.
     *
     * @see MultilayerPlane#setLayerVector
     */
    public void setLayerVector(Vector<MapLayer> layers) {
        super.setLayerVector(layers);
        fireMapChanged();
    }

    /**
     * Calls super method, and additionally fires a {@link MapChangedEvent}.
     *
     * @see MultilayerPlane#swapLayerUp
     */
    public void swapLayerUp(int index) {
        super.swapLayerUp(index);
        fireMapChanged();
        fireLayerMoved(index, index + 1);
    }

    /**
     * Calls super method, and additionally fires a {@link MapChangedEvent}.
     *
     * @see MultilayerPlane#swapLayerDown
     */
    public void swapLayerDown(int index) {
        super.swapLayerDown(index);
        fireMapChanged();
        fireLayerMoved(index, index - 1);
    }

    /**
     * Calls super method, and additionally fires a {@link MapChangedEvent}.
     *
     * @see MultilayerPlane#mergeLayerDown
     */
    public void mergeLayerDown(int index) {
        super.mergeLayerDown(index);
        fireMapChanged();
    }

    /**
     * Calls super method, and additionally fires a {@link MapChangedEvent}.
     *
     * @see MultilayerPlane#resize
     */
    public void resize(int width, int height, int dx, int dy) {
        super.resize(width, height, dx, dy);
        fireMapChanged();
    }

    public Iterator<MapLayer> getLayersSpecial() {
        return specialLayers.iterator();
    }

    /**
     * Get the tile set that matches the given global tile id, only to be used
     * when loading a map.
     *
     * @param gid a global tile id
     * @return the tileset containing the tile with the given global tile id,
     * or <code>null</code> when no such tileset exists
     */
    public Tileset findTileSetForTileGID(int gid) {
        Tileset has = null;
        for (Tileset tileset : tilesets) {
            if (tileset.getFirstGid() <= gid) {
                has = tileset;
            }
        }
        return has;
    }

    /**
     * Returns width of map in tiles.
     *
     * @return int
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Returns height of map in tiles.
     *
     * @return int
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Returns wether the given tile coordinates fall within the map
     * boundaries.
     *
     * @param x The tile-space x-coordinate
     * @param y The tile-space y-coordinate
     * @return <code>true</code> if the point is within the map boundaries,
     * <code>false</code> otherwise
     */
    public boolean contains(int x, int y) {
        return x >= 0 && y >= 0 && x < bounds.width && y < bounds.height;
    }

    /**
     * Swaps the tile sets at the given indices.
     */
    public void swapTileSets(int index0, int index1) {
        if (index0 == index1) return;
        Tileset set = tilesets.get(index0);
        tilesets.set(index0, tilesets.get(index1));
        tilesets.set(index1, set);

        if (index0 > index1) {
            int temp = index1;
            index1 = index0;
            index0 = temp;
        }

        fireTilesetsSwapped(index0, index1);
    }

    /**
     * Returns string describing the map. The form is <code>Map[width x height
     * x layers][tileWidth x tileHeight]</code>, for example <code>
     * Map[64x64x2][24x24]</code>.
     *
     * @return string describing map
     */
    public String toString() {
        return "Map[" + bounds.width + "x" + bounds.height + "x" +
                getTotalLayers() + "][" + Constants.TILE_WIDTH + "x" + Constants.TILE_HEIGHT + "]";
    }


    public void layerChanged(MapLayer layerIndex, MapLayerChangeEvent e) {
        fireLayerChanged(findLayerIndex(layerIndex), e);
    }

}
