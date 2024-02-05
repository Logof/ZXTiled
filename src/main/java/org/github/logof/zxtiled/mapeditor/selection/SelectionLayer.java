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

package org.github.logof.zxtiled.mapeditor.selection;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.util.TiledConfiguration;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.prefs.Preferences;

/**
 * A layer used to keep track of a selected area in another layer. To
 * Achieve this, the SelectionLayer keeps a reference to a parent layer
 * which it uses to determine tile dimensions and other things
 */
public class SelectionLayer extends TileLayer {
    @Setter
    @Getter
    private Color highlightColor;
    private Tile selTile;
    private Area selection;
    private MapLayer parentLayer;

    public SelectionLayer(MapLayer parent) {
        super(parent.getWidth(), parent.getHeight());
        parentLayer = parent;
        init();
    }

    public SelectionLayer(int width, int height, int tileWidth, int tileHeight) {
        super(width, height);
        parentLayer = null;
        init();
    }

    public void setParent(MapLayer layer) {
        this.parentLayer = layer;
    }

    private void init() {
        Preferences prefs = TiledConfiguration.root();
        try {
            highlightColor = Color.decode(prefs.get("selectionColor", "#0000FF"));
        } catch (NumberFormatException e) {
            highlightColor = Color.blue;
        }

        selTile = new Tile();
        selection = new Area();
    }

    @Override
    public boolean isViewPlaneInfinitelyFarAway() {
        if (parentLayer == null)
            return super.isViewPlaneInfinitelyFarAway();
        else
            return parentLayer.isViewPlaneInfinitelyFarAway();
    }

    public float getViewPlaneDistance() {
        if (parentLayer == null) {
            return super.getViewPlaneDistance();
        } else {
            return parentLayer.getViewPlaneDistance();
        }
    }


    @Override
    public int getWidth() {
        if (parentLayer == null) {
            return super.getWidth();
        } else {
            return parentLayer.getWidth();
        }
    }

    /**
     * Returns the selected area.
     *
     * @return the selected area
     */
    public Area getSelectedArea() {
        return selection;
    }

    /**
     * Returns the bounds of the selected area.
     *
     * @return A Rectangle instance
     * @see Area#getBounds()
     */
    public Rectangle getSelectedAreaBounds() {
        return selection.getBounds();
    }

    /**
     * Adds the given area via a union
     *
     * @param area The Area to union with the current selection
     * @see Area#add(java.awt.geom.Area)
     */
    public void add(Area area) {
        selection.add(area);
        fillRegion(selection, selTile);
    }

    /**
     * Deselects the given area. This substracts the given area from the
     * existing selected area.
     *
     * @param area the Area to deselect
     */
    public void subtract(Area area) {
        clearRegion(area);
        selection.subtract(area);
    }

    /**
     * Sets the selected area to the given Shape.
     *
     * @param region
     */
    public void selectRegion(Shape region) {
        clearRegion(selection);
        selection = new Area(region);
        fillRegion(selection, selTile);
    }

    /**
     * Selects only the given tile location (adds it to the selection
     * if one exists)
     *
     * @param tx
     * @param ty
     */
    public void select(int tx, int ty) {
        setTileAt(tx, ty, selTile);

        Area a = new Area(new Rectangle2D.Double(tx, ty, 1, 1));

        if (selection == null) {
            selection = a;
        } else {
            if (!selection.contains(tx, ty)) {
                selection.add(a);
            }
        }
    }

    private void fillRegion(Area region, Tile fill) {
        Rectangle bounded = region.getBounds();
        for (int i = bounded.y; i < bounded.y + bounded.height; i++) {
            for (int j = bounded.x; j < bounded.x + bounded.width; j++) {
                if (region.contains(j, i)) {
                    setTileAt(j + bounds.x, i + bounds.y, fill);
                } else {
                    setTileAt(j + bounds.x, i + bounds.y, null);
                }
            }
        }
    }

    private void clearRegion(Area region) {
        fillRegion(region, null);
    }

    /**
     * Inverts the selected area.
     */
    public void invert() {
        selection.exclusiveOr(new Area(bounds));

        for (int i = bounds.y; i < bounds.y + bounds.height; i++) {
            for (int j = bounds.x; j < bounds.x + bounds.width; j++) {
                if (selection.contains(j, i)) {
                    setTileAt(j, i, selTile);
                } else {
                    setTileAt(j, i, null);
                }
            }
        }
    }
}
