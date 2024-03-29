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
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * The core class for our tiles.
 *
 * @version $Id$
 */
@Getter
public class Tile {
    private Tileset tileset;
    private Image internalImage;
    private Image scaledImage;
    // TODO есть подозрение, что это одно и то же
    private int id = -1;
    private int tileImageId = -1;

    private double myZoom = 1.0;
    @Setter
    private Properties properties;


    public Tile() {
        properties = new Properties();
    }

    public Tile(Tileset set) {
        this();
        setTileSet(set);
    }

    /**
     * Copy constructor
     *
     * @param tile Tile
     */
    public Tile(Tile tile) {
        properties = (Properties) tile.properties.clone();
        tileImageId = tile.tileImageId;
        tileset = tile.tileset;
        if (tileset != null) {
            scaledImage = getImage().getScaledInstance(
                    -1, -1, Image.SCALE_DEFAULT);
        }
    }

    public void setId(int i) {
        if (i >= 0) {
            id = i;
        }
    }

    /**
     * Returns the global tile id by adding the tile id to the map-assigned.
     *
     * @return id
     */
    public int getGid() {
        if (tileset != null) {
            return id + tileset.getFirstGid();
        }
        return id;
    }

    /**
     * Sets the parent tileset for a tile. If the tile is already
     * a member of a tileset, and this method is called with a different
     * tileset as argument, the tile image is transferred to the new tileset.
     *
     * @param tileset
     */
    public void setTileSet(Tileset tileset) {
        if (this.tileset != null && this.tileset != tileset) {
            setImage(tileset.addImage(getImage()));
        } else {
            if (internalImage != null) {
                setImage(tileset.addImage(internalImage));
                internalImage = null;
            }
        }
        this.tileset = tileset;
    }

    /**
     * This drawing function handles drawing the tile image at the
     * specified zoom level. It will attempt to use a cached copy,
     * but will rescale if the requested zoom does not equal the
     * current cache zoom.
     *
     * @param graphics Graphics instance to draw to
     * @param x        x-coordinate to draw tile at
     * @param y        y-coordinate to draw tile at
     * @param zoom     Zoom level to draw the tile
     */
    public void drawRaw(Graphics graphics, int x, int y, double zoom) {
        Image img = getScaledImage(zoom);
        if (img != null) {
            graphics.drawImage(img, x, y - img.getHeight(null), null);
        }
    }

    /**
     * Draws the tile at the given pixel coordinates in the given
     * graphics context, and at the given zoom level
     *
     * @param graphics
     * @param x
     * @param y
     * @param zoom
     */
    public void draw(Graphics graphics, int x, int y, double zoom) {
        // Invoke raw draw function
        drawRaw(graphics, x, y, zoom);
    }

    public int getWidth() {
        if (tileset != null) {
            Dimension d = tileset.getImageDimensions(tileImageId);
            return d.width;
        } else if (internalImage != null) {
            return internalImage.getWidth(null);
        }
        return 0;
    }

    public int getHeight() {
        if (tileset != null) {
            Dimension d = tileset.getImageDimensions(tileImageId);
            return d.height;
        } else if (internalImage != null) {
            return internalImage.getHeight(null);
        }
        return 0;
    }

    public int getImageId() {
        return tileImageId;
    }

    /**
     * Returns the tile image for this Tile.
     *
     * @return Image
     */
    public Image getImage() {
        if (tileset != null) {
            return tileset.getImageById(tileImageId);
        } else {
            return internalImage;
        }
    }

    /**
     * Changes the image of the tile as long as it is not null.
     *
     * @param i the new image of the tile
     */
    public void setImage(Image i) {
        if (tileset != null) {
            tileset.overlayImage(tileImageId, i);
        } else {
            internalImage = i;
        }
    }

    public void setImage(int id) {
        tileImageId = id;
    }

    /**
     * Returns a scaled instance of the tile image. Using a MediaTracker
     * instance, this function waits until the scaling operation is done.
     * <p/>
     * Internally it caches the scaled image in order to optimize the common
     * case, where the same scale is requested as the last time.
     *
     * @param zoom the requested zoom level
     * @return Image
     */
    public Image getScaledImage(double zoom) {
        if (zoom == 1.0) {
            return getImage();
        } else if (zoom == myZoom && scaledImage != null) {
            return scaledImage;
        } else {
            Image img = getImage();
            if (img != null) {
                scaledImage = img.getScaledInstance(
                        (int) (getWidth() * zoom), (int) (getHeight() * zoom),
                        BufferedImage.SCALE_SMOOTH);

                MediaTracker mediaTracker = new MediaTracker(new Canvas());
                mediaTracker.addImage(scaledImage, 0);
                try {
                    mediaTracker.waitForID(0);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
                mediaTracker.removeImage(scaledImage);
                myZoom = zoom;
                return scaledImage;
            }
        }
        return null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Tile " + id + " (" + getWidth() + "x" + getHeight() + ")";
    }
}