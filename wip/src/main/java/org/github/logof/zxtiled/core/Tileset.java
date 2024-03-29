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
import lombok.NonNull;
import lombok.Setter;
import org.github.logof.zxtiled.core.event.TilesetChangedEvent;
import org.github.logof.zxtiled.mapeditor.cutter.BasicTileCutter;
import org.github.logof.zxtiled.mapeditor.cutter.TileCutter;
import org.github.logof.zxtiled.util.NumberedSet;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

/**
 * todo: Update documentation
 * <p>TileSet handles operations on tiles as a set, or group. It has several
 * advanced internal functions aimed at reducing unnecessary data replication.
 * A 'tile' is represented internally as two distinct pieces of data. The
 * first and most important is a {@link Tile} object, and these are held in
 * a {@link Vector}.</p>
 *
 * <p>The other is the tile image.</p>
 */
public class Tileset {
    private String base;
    private final NumberedSet tiles;
    private final NumberedSet images;
    @Setter
    @Getter
    private int firstGid;
    private long tileBmpFileLastModified;
    private TileCutter tileCutter;
    private Rectangle tileDimensions;
    @Getter
    private int tilesPerRow;
    private String externalSource;
    private File tileBmpFile;
    @Getter
    private String name;
    private Properties defaultTileProperties;
    private Image tileSetImage;
    private final LinkedList<TilesetChangeListener> tilesetChangeListeners;
    private final Map<Integer, String> imageSources = new HashMap<>();

    /**
     * Default constructor
     */
    public Tileset() {
        tiles = new NumberedSet();
        images = new NumberedSet();
        tileDimensions = new Rectangle();
        defaultTileProperties = new Properties();
        tilesetChangeListeners = new LinkedList<>();
    }

    /**
     * Creates a tileset from a tileset image file.
     *
     * @param imgFilename
     * @param cutter
     * @throws IOException
     * @see Tileset#importTileBitmap(BufferedImage, TileCutter)
     */
    public void importTileBitmap(String imgFilename, TileCutter cutter) throws IOException {
        setTilesetImageFilename(imgFilename);

        Image image = ImageIO.read(new File(imgFilename));
        if (image == null) {
            throw new IOException("Failed to load " + tileBmpFile);
        }

        BufferedImage buffered = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        buffered.getGraphics().drawImage(image, 0, 0, null);

        importTileBitmap(buffered, cutter);
    }

    /**
     * Creates a tileset from a buffered image. Tiles are cut by the passed
     * cutter.
     *
     * @param tileBmp the image to be used, must not be null
     * @param cutter  the tile cutter, must not be null
     */
    private void importTileBitmap(@NonNull BufferedImage tileBmp, @NonNull TileCutter cutter) {
        tileCutter = cutter;
        tileSetImage = tileBmp;

        cutter.setImage(tileBmp);

        tileDimensions = new Rectangle(cutter.getTileDimensions());
        if (cutter instanceof BasicTileCutter) {
            BasicTileCutter basicTileCutter = (BasicTileCutter) cutter;
            tilesPerRow = basicTileCutter.getTilesPerRow();
        }

        Image tile = cutter.getNextTile();
        while (tile != null) {
            Tile newTile = new Tile();
            newTile.setImage(addImage(tile));
            addNewTile(newTile);
            tile = cutter.getNextTile();
        }
    }

    /**
     * Refreshes a tileset from a tileset image file.
     *
     * @throws IOException
     * @see Tileset#importTileBitmap(BufferedImage, TileCutter)
     */
    private void refreshImportedTileBitmap() throws IOException {
        String imgFilename = tileBmpFile.getPath();

        Image image = ImageIO.read(new File(imgFilename));
        if (image == null) {
            throw new IOException("Failed to load " + tileBmpFile);
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        BufferedImage buffered = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        buffered.getGraphics().drawImage(image, 0, 0, null);

        refreshImportedTileBitmap(buffered);
    }

    /**
     * Refreshes a tileset from a buffered image. Tiles are cut by the passed
     * cutter.
     *
     * @param tileBmp the image to be used, must not be null
     */
    private void refreshImportedTileBitmap(@NonNull BufferedImage tileBmp) {
        tileCutter.reset();
        tileCutter.setImage(tileBmp);

        tileSetImage = tileBmp;
        tileDimensions = new Rectangle(tileCutter.getTileDimensions());

        int id = 0;
        Image tile = tileCutter.getNextTile();
        while (tile != null) {
            int imgId = getTile(id).getTileImageId();
            overlayImage(imgId, tile);
            tile = tileCutter.getNextTile();
            id++;
        }

        fireTilesetChanged();
    }

    public void checkUpdate() throws IOException {
        if (tileBmpFile != null &&
                tileBmpFile.lastModified() > tileBmpFileLastModified) {
            refreshImportedTileBitmap();
            tileBmpFileLastModified = tileBmpFile.lastModified();
        }
    }

    /**
     * Sets the filename of the tileset image. Doesn't change the tileset in
     * any other way.
     *
     * @param name
     */
    public void setTilesetImageFilename(String name) {
        if (Objects.nonNull(name)) {
            tileBmpFile = new File(name);
            tileBmpFileLastModified = tileBmpFile.lastModified();
        } else {
            tileBmpFile = null;
        }
    }

    /**
     * Adds the tile to the set, setting the id of the tile only if the current
     * value of id is -1.
     *
     * @param tile the tile to add
     * @return int The <b>local</b> id of the tile
     */
    public int addTile(Tile tile) {
        if (tile.getId() < 0) {
            tile.setId(tiles.getMaxId() + 1);
        }

        if (tileDimensions.width < tile.getWidth()) {
            tileDimensions.width = tile.getWidth();
        }

        if (tileDimensions.height < tile.getHeight()) {
            tileDimensions.height = tile.getHeight();
        }

        // Add any default properties
        // TODO: use parent properties instead?
        tile.getProperties().putAll(defaultTileProperties);

        tiles.put(tile.getId(), tile);
        tile.setTileSet(this);

        fireTilesetChanged();

        return tile.getId();
    }

    /**
     * This method takes a new Tile object as argument, and in addition to
     * the functionality of <code>addTile()</code>, sets the id of the tile
     * to -1.
     *
     * @param tile the new tile to add.
     * @see Tileset#addTile(Tile)
     */
    public void addNewTile(Tile tile) {
        tile.setId(-1);
        addTile(tile);
    }

    /**
     * Removes a tile from this tileset. Does not invalidate other tile
     * indices. Removal is simply setting the reference at the specified
     * index to <b>null</b>.
     * <p>
     * todo: Fix the behaviour of this function? It actually does seem to
     * todo: invalidate other tile indices due to implementation of
     * todo: NumberedSet.
     *
     * @param i the index to remove
     */
    public void removeTile(int i) {
        tiles.remove(i);
        fireTilesetChanged();
    }

    /**
     * Returns the amount of tiles in this tileset.
     *
     * @return the amount of tiles in this tileset
     */
    public int size() {
        return tiles.size();
    }

    /**
     * Returns the maximum tile id.
     *
     * @return the maximum tile id, or -1 when there are no tiles
     */
    public int getMaxTileId() {
        return tiles.getMaxId();
    }

    /**
     * Returns an iterator over the tiles in this tileset.
     *
     * @return an iterator over the tiles in this tileset.
     */
    public Iterator iterator() {
        return tiles.iterator();
    }

    /**
     * Generates a vector that removes the gaps that can occur if a tile is
     * removed from the middle of a set of tiles. (Maps tiles contiguously)
     *
     * @return a {@link Vector} mapping ordered set location to the next
     * non-null tile
     */
    public Vector<Tile> generateGaplessVector() {
        Vector<Tile> tales = new Vector<>();

        for (int i = 0; i <= getMaxTileId(); i++) {
            if (getTile(i) != null) {
                tales.add(getTile(i));
            }
        }
        return tales;
    }

    /**
     * Returns the width of tiles in this tileset. All tiles in a tileset
     * should be the same width, and the same as the tile width of the map the
     * tileset is used with.
     *
     * @return int - The maximum tile width
     */
    public int getTileWidth() {
        return tileDimensions.width;
    }

    /**
     * Returns the tile height of tiles in this tileset. Not all tiles in a
     * tileset are required to have the same height, but the height should be
     * at least the tile height of the map the tileset is used with.
     * <p>
     * If there are tiles with varying heights in this tileset, the returned
     * height will be the maximum.
     *
     * @return the max height of the tiles in the set
     */
    public int getTileHeight() {
        return tileDimensions.height;
    }

    /**
     * Gets the tile with <b>local</b> id <code>i</code>.
     *
     * @param i local id of tile
     * @return A tile with local id <code>i</code> or <code>null</code> if no
     * tile exists with that id
     */
    public Tile getTile(int i) {
        try {
            return (Tile) tiles.get(i);
        } catch (ArrayIndexOutOfBoundsException a) {
        }
        return null;
    }

    /**
     * Returns the first non-null tile in the set.
     *
     * @return The first tile in this tileset, or <code>null</code> if none
     * exists.
     */
    public Tile getFirstTile() {
        Tile ret = null;
        int i = 0;
        while (ret == null && i <= getMaxTileId()) {
            ret = getTile(i);
            i++;
        }
        return ret;
    }

    /**
     * Returns the source of this tileset.
     *
     * @return a filename if tileset is external or <code>null</code> if
     * tileset is internal.
     */
    public String getSource() {
        return externalSource;
    }

    /**
     * Sets the URI path of the external source of this tile set. By setting
     * this, the set is implied to be external in all other operations.
     *
     * @param source a URI of the tileset image file
     */
    public void setSource(String source) {
        String oldSource = externalSource;
        externalSource = source;

        fireSourceChanged(oldSource, source);
    }

    /**
     * Returns the base directory for the tileset
     *
     * @return a directory in native format as given in the tileset file or tag
     */
    public String getBaseDir() {
        return base;
    }

    /**
     * Sets the base directory for the tileset
     *
     * @param base a String containing the native format directory
     */
    public void setBaseDir(String base) {
        this.base = base;
    }

    /**
     * Returns the filename of the tileset image.
     *
     * @return the filename of the tileset image, or <code>null</code> if this
     * tileset doesn't reference a tileset image
     */
    public String getTileBmpFile() {
        if (tileBmpFile != null) {
            try {
                return tileBmpFile.getCanonicalPath();
            } catch (IOException e) {
            }
        }

        return null;
    }

    /**
     * Sets the name of this tileset.
     *
     * @param name the new name for this tileset
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        fireNameChanged(oldName, name);
    }

    /**
     * @return the name of the tileset, and the total tiles
     */
    public String toString() {
        return getName() + " [" + size() + "]";
    }


    /**
     * Returns the number of images in the set.
     *
     * @return the number of images in the set
     */
    public int getTotalImages() {
        return images.size();
    }

    /**
     * @return an Enumeration of the image ids
     */
    public Enumeration<String> getImageIds() {
        Vector<String> v = new Vector<>();
        for (int id = 0; id <= images.getMaxId(); ++id) {
            if (images.containsId(id)) {
                v.add(Integer.toString(id));
            }
        }
        return v.elements();
    }

    // TILE IMAGE CODE

    /**
     * This function uses the CRC32 checksums to find the cached version of the
     * image supplied.
     *
     * @param i an Image object
     * @return returns the id of the given image, or -1 if the image is not in
     * the set
     */
    public int getIdByImage(Image i) {
        return images.indexOf(i);
    }

    /**
     * @param id
     * @return the image identified by the key, or <code>null</code> when
     * there is no such image
     */
    public Image getImageById(int id) {
        return (Image) images.get(id);
    }

    /**
     * @param id
     * @return the source path registered with this image ID. May be null
     * even if an image is registered for this ID, because a source does
     * not need to be registered (this is especially true for imbedded
     * images)
     * @return String
     */
    public String getImageSource(int id) {
        return imageSources.get(id);
    }

    /**
     * Overlays the image in the set referred to by the given key.
     *
     * @param id
     * @param image
     */
    public void overlayImage(int id, Image image) {
        images.put(id, image);
    }

    /**
     * Returns the dimensions of an image as specified by the id.
     *
     * @param id the image id
     * @return dimensions of image with referenced by given key
     * @deprecated Unless somebody can explain the purpose of this function in
     * its documentation, I consider this function deprecated. It
     * is only used by tiles, but they should in my opinion just
     * use their "internalImage". - Bjorn
     */
    public Dimension getImageDimensions(int id) {
        Image img = (Image) images.get(id);
        if (img != null) {
            return new Dimension(img.getWidth(null), img.getHeight(null));
        } else {
            return new Dimension(0, 0);
        }
    }

    /**
     * Adds the specified image to the image cache. If the image already exists
     * in the cache, returns the id of the existing image. If it does not
     * exist, this function adds the image and returns the new id.
     *
     * @param image       the java.awt.Image to add to the image cache
     * @param imageSource the path of the source image or null if none
     *                    is to be specified.
     * @return the id as an <code>int</code> of the image in the cache
     */
    public int addImage(Image image, String imageSource) {
        int id = images.findOrAdd(image);
        if (imageSource != null) {
            imageSources.put(id, imageSource);
        }
        return id;
    }

    public int addImage(Image image) {
        return addImage(image, null);
    }

    public int addImage(Image image, int id, String imgSource) {
        if (imgSource != null) {
            imageSources.put(id, imgSource);
        }

        return images.put(id, image);
    }

    public void removeImage(int id) {
        images.remove(id);
        imageSources.remove(id);
    }

    /**
     * Checks whether each image has a one to one relationship with the tiles.
     *
     * @return <code>true</code> if each image is associated with one and only
     * one tile, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean isOneForOne() {
        Iterator itr = iterator();

        for (int id = 0; id <= images.getMaxId(); ++id) {
            int relations = 0;
            itr = iterator();

            while (itr.hasNext()) {
                Tile t = (Tile) itr.next();
                // todo: move the null check back into the iterator?
                if (t != null && t.getImageId() == id) {
                    relations++;
                }
            }
            if (relations != 1) {
                return false;
            }
        }
        return true;
    }

    public void setDefaultProperties(Properties defaultSetProperties) {
        defaultTileProperties = defaultSetProperties;
    }

    public void addTilesetChangeListener(TilesetChangeListener listener) {
        tilesetChangeListeners.add(listener);
    }

    public void removeTilesetChangeListener(TilesetChangeListener listener) {
        tilesetChangeListeners.remove(listener);
    }

    private void fireTilesetChanged() {
        TilesetChangedEvent event = new TilesetChangedEvent(this);
        for (TilesetChangeListener listener : tilesetChangeListeners) {
            listener.tilesetChanged(event);
        }
    }

    private void fireNameChanged(String oldName, String newName) {
        TilesetChangedEvent event = new TilesetChangedEvent(this);
        for (TilesetChangeListener listener : tilesetChangeListeners) {
            listener.nameChanged(event, oldName, newName);
        }
    }

    private void fireSourceChanged(String oldSource, String newSource) {
        TilesetChangedEvent event = new TilesetChangedEvent(this);
        for (TilesetChangeListener listener : tilesetChangeListeners) {
            listener.sourceChanged(event, oldSource, newSource);
        }
    }
}
