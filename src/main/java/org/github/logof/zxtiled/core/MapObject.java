/*
 *  Tiled Map Editor, (c) 2004-2008
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
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.enums.EnemyEnum;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * An object occupying an {@link ObjectLayer}.
 */
public class MapObject implements Cloneable {
    @Setter
    @Getter
    private Properties properties = new Properties();
    @Getter
    @Setter
    private ObjectLayer objectLayer;
    @Setter
    @Getter
    private Rectangle bounds;
    @Setter
    @Getter
    private String name = "Object";
    @Setter
    @Getter
    private EnemyEnum type;
    @Getter
    private String imageSource = "";
    private Image image;
    private Image scaledImage;
    @Getter
    private final int screenNumber;
    @Getter
    private final int coordinateXAt;
    @Getter
    private final int coordinateYAt;

    @Getter
    @Setter
    private Rectangle path;

    public MapObject(int x, int y, int screenNumber) {
        this.bounds = new Rectangle(
                x * Constants.TILE_WIDTH,
                y * Constants.TILE_HEIGHT,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT);
        this.screenNumber = screenNumber;
        this.coordinateXAt = x;
        this.coordinateYAt = y;
    }

    public Object clone() throws CloneNotSupportedException {
        MapObject clone = (MapObject) super.clone();
        clone.bounds = new Rectangle(bounds);
        clone.properties = (Properties) properties.clone();
        return clone;
    }

    public void setImageSource(String source) {
        if (imageSource.equals(source))
            return;

        imageSource = source;

        // Attempt to read the image
        if (!imageSource.isEmpty()) {
            try {
                image = ImageIO.read(new File(imageSource));
            } catch (IOException e) {
                image = null;
            }
        } else {
            image = null;
        }
        scaledImage = null;
    }

    /**
     * Returns the image to be used when drawing this object. This image is
     * scaled to the size of the object.
     *
     * @param zoom the requested zoom level of the image
     * @return the image to be used when drawing this object
     */
    public Image getImage(double zoom) {
        if (image == null) {
            return null;
        }

        final int zoomedWidth = (int) (getWidth() * zoom);
        final int zoomedHeight = (int) (getHeight() * zoom);

        if (scaledImage == null || scaledImage.getWidth(null) != zoomedWidth
                || scaledImage.getHeight(null) != zoomedHeight) {
            scaledImage = image.getScaledInstance(zoomedWidth, zoomedHeight,
                    Image.SCALE_SMOOTH);
        }

        return scaledImage;
    }

    public int getX() {
        return bounds.x;
    }

    public void setX(int x) {
        bounds.x = x;
    }

    public int getY() {
        return bounds.y;
    }

    public void setY(int y) {
        bounds.y = y;
    }

    public void translate(int dx, int dy) {
        bounds.translate(dx, dy);
    }

    public int getWidth() {
        return bounds.width;
    }

    public void setWidth(int width) {
        bounds.width = width;
    }

    public int getHeight() {
        return bounds.height;
    }

    public void setHeight(int height) {
        bounds.height = height;
    }

    public String toString() {
        return type + " (" + getX() + "," + getY() + ")";
    }
}
