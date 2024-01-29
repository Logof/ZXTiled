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

package org.github.logof.zxtiled.mapeditor.cutter;

import lombok.Setter;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Cuts tiles from a tileset image according to a regular rectangular pattern.
 * Supports a variable spacing between tiles and a margin around them.
 */
public class BasicTileCutter implements TileCutter {
    private final int tileWidth = 16;
    private final int tileHeight = 16;
    private int nextX, nextY;
    @Setter
    private BufferedImage image;

    public BasicTileCutter() {
        reset();
    }

    public String getName() {
        return "Basic";
    }

    public Image getNextTile() {
        if (nextY + tileHeight <= image.getHeight()) {
            BufferedImage tile = image.getSubimage(nextX, nextY, tileWidth, tileHeight);
            nextX += tileWidth;
            if (nextX + tileWidth > image.getWidth()) {
                nextX = 0;
                nextY += tileHeight;
            }
            return tile;
        }
        return null;
    }

    public void reset() {
        nextX = 0;
        nextY = 0;
    }

    public Dimension getTileDimensions() {
        return new Dimension(tileWidth, tileHeight);
    }

    public int getTilesPerRow() {
        return image.getWidth() / tileWidth;
    }
}
