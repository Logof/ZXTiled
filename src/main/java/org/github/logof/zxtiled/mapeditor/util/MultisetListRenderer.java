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

package org.github.logof.zxtiled.mapeditor.util;

import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * This list renderer is used for rendering a list of tiles associated with a
 * certain map. The list renderer produces {@link ImageIcon} instances on
 * demand and caches them in a {@link HashMap}.
 * <p>
 * todo: check whether assuming all tiles have an image is safe
 *
 * @version $Id$
 */
public class MultisetListRenderer extends DefaultListCellRenderer {
    /**
     * The icon to show for tilesets.
     */
    private final Icon setIcon = Resources.getIcon("icon/source.png");

    /**
     * The hash map used to match indexes to icons.
     */
    private final HashMap<Integer, Icon> tileImages = new HashMap<Integer, Icon>();

    /**
     * The zoom level used for the tile image icons.
     */
    private final double zoom;

    /**
     * Creates the list renderer for rendering a list of tiles.
     *
     * @param zoom the zoom level at which the tiles will be shown
     */
    public MultisetListRenderer(double zoom) {
        this.zoom = zoom;
    }

    /**
     * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        // Let the default list cell renderer do most of the work
        super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        // Attempt to set an appropriate icon
        if (value instanceof Tile && index >= 0) {
            Tile tile = (Tile) value;
            if (!isSelected || zoom == 1) {
                // Use cached ImageIcon instance
                if (tileImages.containsKey(index)) {
                    setIcon(tileImages.get(index));
                } else {
                    Icon icon = new ImageIcon(tile.getScaledImage(zoom));
                    setIcon(icon);
                    tileImages.put(index, icon);
                }
            } else {
                // Selected entry always uses unscaled image
                setIcon(new ImageIcon(tile.getImage()));
            }
        } else if (value instanceof Tileset) {
            setIcon(setIcon);
        }

        return this;
    }
}
