package org.github.logof.zxtiled.mapeditor.listener;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MapEditorListSelectionListener implements ListSelectionListener {

    private final MapEditor mapEditor;

    public MapEditorListSelectionListener(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        int selectedRow = mapEditor./*getDataPanel().*/getLayerTable().getSelectedRow();

        // At the moment, this can only be a new layer selection
        if (mapEditor.getCurrentTileMap() != null && selectedRow >= 0) {
            mapEditor.setCurrentLayerIndex(2 - selectedRow - 1);
        } else {
            mapEditor.setCurrentLayerIndex(-1);
        }
        mapEditor.updateLayerOperations();

    }
}
