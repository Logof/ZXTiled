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
 *
 *  This class is based on TilesetChooserTabbedPane from Stendhal Map Editor
 *  by Matthias Totz <mtotz@users.sourceforge.net>
 */

package org.github.logof.zxtiled.mapeditor.gui;

import org.github.logof.zxtiled.core.MapChangeAdapter;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.core.TilesetChangeListener;
import org.github.logof.zxtiled.core.event.MapChangedEvent;
import org.github.logof.zxtiled.core.event.TilesetChangedEvent;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.brush.CustomBrush;
import org.github.logof.zxtiled.mapeditor.util.TileRegionSelectionEvent;
import org.github.logof.zxtiled.mapeditor.util.TileSelectionEvent;
import org.github.logof.zxtiled.mapeditor.util.TileSelectionListener;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

/**
 * Shows one tab for each Tileset.
 *
 * @version $Id$
 */
public class TabbedTilesetsPane extends JTabbedPane implements TileSelectionListener {
    /**
     * Map of tile sets to tile palette panels
     */
    private final HashMap<Tileset, TilePalettePanel> tilePanels = new HashMap<>();
    private final MyChangeListener listener = new MyChangeListener();
    private final MapEditor mapEditor;
    private TileMap tileMap;

    /**
     * Constructor.
     *
     * @param mapEditor reference to the MapEditor instance, used to change
     *                  the current tile and brush
     */
    public TabbedTilesetsPane(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    /**
     * Sets the tiles panes to the the ones from this map.
     *
     * @param tileMap the map of which to display the tilesets
     */
    public void setMap(TileMap tileMap) {
        if (this.tileMap == tileMap) {
            return;
        }

        if (this.tileMap != null) {
            this.tileMap.removeMapChangeListener(listener);
        }

        if (tileMap == null) {
            removeAll();
        } else {
            recreateTabs(tileMap.getTilesets());
            tileMap.addMapChangeListener(listener);
        }

        this.tileMap = tileMap;
    }

    /**
     * Creates the panels for the tilesets.
     *
     * @param tilesets the list of tilesets to create panels for
     */
    private void recreateTabs(List<Tileset> tilesets) {
        // Stop listening to the tile palette panels and their tilesets
        for (TilePalettePanel panel : tilePanels.values()) {
            panel.removeTileSelectionListener(this);
            panel.getTileset().removeTilesetChangeListener(listener);
        }
        tilePanels.clear();

        // Remove all tabs
        removeAll();

        if (tilesets != null) {
            // Add a new tab for each tileset of the map
            for (Tileset tileset : tilesets) {
                if (tileset != null) {
                    addTabForTileset(tileset);
                }
            }
        }
    }

    /**
     * Adds a tab with a {@link TilePalettePanel} for the given tileset.
     *
     * @param tileset the given tileset
     */
    private void addTabForTileset(Tileset tileset) {
        tileset.addTilesetChangeListener(listener);
        TilePalettePanel tilePanel = new TilePalettePanel();
        tilePanel.setTileset(tileset);
        tilePanel.addTileSelectionListener(this);
        JScrollPane paletteScrollPane = new JScrollPane(tilePanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        addTab(tileset.getName(), paletteScrollPane);
        tilePanels.put(tileset, tilePanel);
    }

    /**
     * Informs the editor of the new tile.
     */
    public void tileSelected(TileSelectionEvent event) {
        mapEditor.setCurrentTile(event.getTile());
    }

    /**
     * Creates a stamp brush from the region contents and sets this as the current brush.
     */
    public void tileRegionSelected(TileRegionSelectionEvent e) {
        mapEditor.setBrush(new CustomBrush(e.getTileRegion()));
    }

    private class MyChangeListener extends MapChangeAdapter implements TilesetChangeListener {

        @Override
        public void tilesetAdded(MapChangedEvent e, Tileset tileset) {
            addTabForTileset(tileset);
        }

        @Override
        public void tilesetRemoved(MapChangedEvent e, int index) {
            JScrollPane scroll = (JScrollPane) getComponentAt(index);
            TilePalettePanel panel = (TilePalettePanel) scroll.getViewport().getView();
            Tileset set = panel.getTileset();
            panel.removeTileSelectionListener(TabbedTilesetsPane.this);
            set.removeTilesetChangeListener(listener);
            tilePanels.remove(set);
            removeTabAt(index);
        }

        @Override
        public void tilesetsSwapped(MapChangedEvent mapChangedEvent, int index0, int index1) {
            int sIndex = getSelectedIndex();

            String title0 = getTitleAt(index0);
            String title1 = getTitleAt(index1);

            Component comp0 = getComponentAt(index0);
            Component comp1 = getComponentAt(index1);

            removeTabAt(index1);
            removeTabAt(index0);

            insertTab(title1, null, comp1, null, index0);
            insertTab(title0, null, comp0, null, index1);

            if (sIndex == index0) {
                sIndex = index1;
            } else if (sIndex == index1) {
                sIndex = index0;
            }

            setSelectedIndex(sIndex);
        }

        public void tilesetChanged(TilesetChangedEvent event) {
        }

        public void nameChanged(TilesetChangedEvent event, String oldName, String newName) {
            Tileset set = event.getTileset();
            int index = tileMap.getTilesets().indexOf(set);

            setTitleAt(index, newName);
        }

        public void sourceChanged(TilesetChangedEvent event, String oldSource, String newSource) {
        }

        public void layerAdded(MapChangedEvent e) {
        }

        public void layerRemoved(MapChangedEvent e) {
        }

        public void layerMoved(MapChangedEvent e) {
        }
    }
}
