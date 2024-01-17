package org.github.logof.zxtiled.mapeditor.listener;

import org.github.logof.zxtiled.core.MapChangeListener;
import org.github.logof.zxtiled.core.TileSet;
import org.github.logof.zxtiled.core.event.MapChangedEvent;
import org.github.logof.zxtiled.core.event.MapLayerChangeEvent;
import org.github.logof.zxtiled.mapeditor.MapEditor;

public class MapEditorMapChangeListener implements MapChangeListener {

    private final MapEditor mapEditor;

    public MapEditorMapChangeListener(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    @Override
    public void mapChanged(MapChangedEvent e) {
        if (e.getMap() == mapEditor.getCurrentTileMap()) {
            mapEditor.getMapScrollPane().setViewportView(mapEditor.getMapView());
            mapEditor.updateLayerTable();
            mapEditor.getMapView().repaint();
        }
    }

    @Override
    public void layerAdded(MapChangedEvent e) {

    }

    @Override
    public void layerRemoved(MapChangedEvent e) {

    }

    @Override
    public void layerMoved(MapChangedEvent e) {

    }

    @Override
    public void layerChanged(MapChangedEvent e, MapLayerChangeEvent layerChangeEvent) {

    }

    @Override
    public void tilesetAdded(MapChangedEvent e, TileSet tileset) {

    }

    @Override
    public void tilesetRemoved(MapChangedEvent e, int index) {
        mapEditor.getMapView().repaint();
    }

    @Override
    public void tilesetsSwapped(MapChangedEvent e, int index0, int index1) {

    }
}
