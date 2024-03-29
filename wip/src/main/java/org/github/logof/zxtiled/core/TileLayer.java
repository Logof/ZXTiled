package org.github.logof.zxtiled.core;

import org.github.logof.zxtiled.exception.LayerLockedException;
import java.awt.*;
import java.awt.geom.Area;
import java.util.HashMap;
import java.util.Properties;

/**
 * A TileLayer is a specialized MapLayer, used for tracking two dimensional tile data.
 */
public class TileLayer extends MapLayer {
    protected Tile[][] map;
    protected HashMap<Object, Properties> tileInstanceProperties = new HashMap<>();

    public TileLayer() {
    }

    /**
     * Construct a TileLayer from the given width and height.
     *
     * @param width  width in tiles
     * @param height height in tiles
     */
    public TileLayer(int width, int height) {
        super(width, height);
    }

    /**
     * Create a tile layer using the given bounds.
     *
     * @param rectangle the bounds of the tile layer.
     */
    public TileLayer(Rectangle rectangle) {
        super(rectangle);
    }

    /**
     * @param tileMap the map this layer is part of
     * @param width   width in tiles
     * @param height  height in tiles
     */
    public TileLayer(TileMap tileMap, int width, int height) {
        super(width, height);
        setMap(tileMap);
    }

    public Properties getTileInstancePropertiesAt(int x, int y) {
        if (!bounds.contains(x, y)) {
            return null;
        }
        Object key = new Point(x, y);
        return tileInstanceProperties.get(key);
    }

    public void setTileInstancePropertiesAt(int x, int y, Properties tip) {
        if (bounds.contains(x, y)) {
            Object key = new Point(x, y);
            tileInstanceProperties.put(key, tip);
        }
    }


    /**
     * Performs a mirroring function on the layer data. Two orientations are
     * allowed: vertical and horizontal.
     * <p>
     * Example: <code>layer.mirror(MapLayer.MIRROR_VERTICAL);</code> will
     * mirror the layer data around a horizontal axis.
     *
     * @param dir the axial orientation to mirror around
     */
    public void mirror(int dir) {
        if (cannotEdit()) {
            return;
        }

        Tile[][] mirror = new Tile[bounds.height][bounds.width];
        for (int y = 0; y < bounds.height; y++) {
            for (int x = 0; x < bounds.width; x++) {
                if (dir == MIRROR_VERTICAL) {
                    mirror[y][x] = map[bounds.height - 1 - y][x];
                } else {
                    mirror[y][x] = map[y][bounds.width - 1 - x];
                }
            }
        }
        map = mirror;
    }

    /**
     * Checks to see if the given Tile is used anywhere in the layer.
     *
     * @param t a Tile object to check for
     * @return <code>true</code> if the Tile is used at least once,
     * <code>false</code> otherwise.
     */
    public boolean isUsed(Tile t) {
        for (int y = 0; y < bounds.height; y++) {
            for (int x = 0; x < bounds.width; x++) {
                if (map[y][x] == t) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEmpty() {
        for (int p = 0; p < 2; p++) {
            for (int y = 0; y < bounds.height; y++) {
                for (int x = p; x < bounds.width; x += 2) {
                    if (map[y][x] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets the bounds (in tiles) to the specified Rectangle. <b>Caution:</b>
     * this causes a reallocation of the data array, and all previous data is
     * lost.
     *
     * @param bounds new new bounds of this tile layer (in tiles)
     * @see MapLayer#setBounds
     */
    protected void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
        map = new Tile[bounds.height][bounds.width];

        // Tile instance properties is null when this method is called from the constructor of MapLayer
        if (tileInstanceProperties != null) {
            tileInstanceProperties.clear();
        }
    }

    /**
     * Creates a diff of the two layers, <code>mapLayer</code> is considered the
     * significant difference.
     *
     * @param mapLayer Layer of map
     * @return A new MapLayer that represents the difference between this
     * layer, and the argument, or <b>null</b> if no difference exists.
     */
    public MapLayer createDiff(MapLayer mapLayer) {
        if (mapLayer == null) {
            return null;
        }

        if (mapLayer instanceof TileLayer) {
            Rectangle r = null;

            for (int y = bounds.y; y < bounds.height + bounds.y; y++) {
                for (int x = bounds.x; x < bounds.width + bounds.x; x++) {
                    if (((TileLayer) mapLayer).getTileAt(x, y) != getTileAt(x, y)) {
                        if (r != null) {
                            r.add(x, y);
                        } else {
                            r = new Rectangle(new Point(x, y));
                        }
                    }
                }
            }

            if (r != null) {
                MapLayer diff = new TileLayer(
                        new Rectangle(r.x, r.y, r.width + 1, r.height + 1));
                diff.copyFrom(mapLayer);
                return diff;
            } else {
                return new TileLayer();
            }
        } else {
            return null;
        }
    }


    /**
     * Removes any occurences of the given tile from this map layer. If layer
     * is locked, an exception is thrown.
     *
     * @param tile the Tile to be removed
     * @throws LayerLockedException when this layer is locked
     */
    public void removeTile(Tile tile) throws LayerLockedException {
        if (getLocked()) {
            throw new LayerLockedException("Attempted to remove tile when this layer is locked.");
        }

        for (int y = 0; y < bounds.height; y++) {
            for (int x = 0; x < bounds.width; x++) {
                if (map[y][x] == tile) {
                    setTileAt(x + bounds.x, y + bounds.y, null);
                }
            }
        }
    }

    /**
     * Sets the tile at the specified position. Does nothing if (tx, ty) falls
     * outside of this layer.
     *
     * @param tx x position of tile
     * @param ty y position of tile
     * @param ti the tile object to place
     */
    public void setTileAt(int tx, int ty, Tile ti) {
        if (bounds.contains(tx, ty) && !getLocked()) {
            map[ty - bounds.y][tx - bounds.x] = ti;
        }
    }

    /**
     * Returns the tile at the specified position.
     *
     * @param tileX Tile-space x coordinate
     * @param tileY Tile-space y coordinate
     * @return tile at position (tileX, tileY) or <code>null</code> when (tileX, tileY) is
     * outside this layer
     */
    public Tile getTileAt(int tileX, int tileY) {
        return (bounds.contains(tileX, tileY))
                ? map[tileY - bounds.y][tileX - bounds.x]
                : null;
    }

    /**
     * @inheritDoc MapLayer#mergeOnto(MapLayer)
     */
    public void mergeOnto(MapLayer other) {
        if (other.cannotEdit()) {
            return;
        }

        for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
            for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
                Tile tile = getTileAt(x, y);
                if (tile != null) {
                    ((TileLayer) other).setTileAt(x, y, tile);
                }
            }
        }
    }

    /**
     * Like mergeOnto, but will only copy the area specified.
     *
     * @param other
     * @param mask
     * @see TileLayer#mergeOnto(MapLayer)
     */
    public void maskedMergeOnto(MapLayer other, Area mask) {
        if (cannotEdit()) {
            return;
        }

        Rectangle boundBox = mask.getBounds();

        for (int y = boundBox.y; y < boundBox.y + boundBox.height; y++) {
            for (int x = boundBox.x; x < boundBox.x + boundBox.width; x++) {
                Tile tile = ((TileLayer) other).getTileAt(x, y);
                if (mask.contains(x, y) && tile != null) {
                    setTileAt(x, y, tile);
                }
            }
        }
    }

    /**
     * Copy data from another layer onto this layer. Unlike mergeOnto,
     * copyFrom() copies the empty cells as well.
     *
     * @param other
     * @see MapLayer#mergeOnto
     */
    public void copyFrom(MapLayer other) {
        if (cannotEdit()) {
            return;
        }

        for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
            for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
                setTileAt(x, y, ((TileLayer) other).getTileAt(x, y));
            }
        }
    }

    /**
     * Like copyFrom, but will only copy the area specified.
     *
     * @param other
     * @param mask
     * @see TileLayer#copyFrom(MapLayer)
     */
    public void maskedCopyFrom(MapLayer other, Area mask) {
        if (cannotEdit()) {
            return;
        }

        Rectangle boundBox = mask.getBounds();

        for (int y = boundBox.y; y < boundBox.y + boundBox.height; y++) {
            for (int x = boundBox.x; x < boundBox.x + boundBox.width; x++) {
                if (mask.contains(x, y)) {
                    setTileAt(x, y, ((TileLayer) other).getTileAt(x, y));
                }
            }
        }
    }

    /**
     * Unlike mergeOnto, copyTo includes the null tile when merging.
     *
     * @param other the layer to copy this layer to
     * @see MapLayer#copyFrom
     * @see MapLayer#mergeOnto
     */
    public void copyTo(MapLayer other) {
        if (other.cannotEdit()) {
            return;
        }

        TileLayer tileLayer;
        try {
            tileLayer = (TileLayer) other;
        } catch (ClassCastException e) {
            return;    // can't copy to this layer
        }

        super.copyTo(other);

        for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
            for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
                tileLayer.setTileAt(x, y, getTileAt(x, y));
            }
        }

    }

    /**
     * Creates a copy of this layer.
     *
     * @return a clone of this layer, as complete as possible
     * @throws CloneNotSupportedException
     * @see Object#clone
     */
    public Object clone() throws CloneNotSupportedException {
        TileLayer clone = (TileLayer) super.clone();

        // Clone the layer data
        clone.map = new Tile[map.length][];
        clone.tileInstanceProperties = new HashMap<>();

        for (int i = 0; i < map.length; i++) {
            clone.map[i] = new Tile[map[i].length];
            System.arraycopy(map[i], 0, clone.map[i], 0, map[i].length);

            for (int j = 0; j < map[i].length; j++) {
                Properties p = getTileInstancePropertiesAt(i, j);

                if (p != null) {
                    Integer key = i + j * bounds.width;
                    clone.tileInstanceProperties.put(key, (Properties) p.clone());
                }
            }
        }

        return clone;
    }

    /**
     * @param width  the new width of the layer
     * @param height the new height of the layer
     * @param dx     the shift in x direction
     * @param dy     the shift in y direction
     * @see MultilayerPlane#resize
     */
    public void resize(int width, int height, int dx, int dy) {
        if (getLocked()) {
            return;
        }

        Tile[][] newMap = new Tile[height][width];
        HashMap<Object, Properties> newTileInstanceProperties = new HashMap<>();

        int maxX = Math.min(width, bounds.width + dx);
        int maxY = Math.min(height, bounds.height + dy);

        for (int x = Math.max(0, dx); x < maxX; x++) {
            for (int y = Math.max(0, dy); y < maxY; y++) {
                newMap[y][x] = getTileAt(x - dx, y - dy);

                Properties tip = getTileInstancePropertiesAt(x - dx, y - dy);
                if (tip != null) {
                    newTileInstanceProperties.put(new Point(x, y), tip);
                }
            }
        }

        map = newMap;
        tileInstanceProperties = newTileInstanceProperties;
        bounds.width = width;
        bounds.height = height;
    }
}
