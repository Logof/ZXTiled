package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.github.logof.zxtiled.view.MapView.ZOOM_NORMAL_SIZE;

public class ZoomNormalAction extends AbstractAction {
    private final MapEditor mapEditor;

    public ZoomNormalAction(MapEditor mapEditor) {
        super(Resources.getString("action.zoom.normal.name"));
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control 0"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.zoom.normal.tooltip"));
        this.mapEditor = mapEditor;
    }

    public void actionPerformed(ActionEvent evt) {
        if (mapEditor.getCurrentTileMap() != null) {
            MapEditorAction.zoomInAction.setEnabled(true);
            MapEditorAction.zoomOutAction.setEnabled(true);
            setEnabled(false);
            mapEditor.getMapView().setZoomLevel(ZOOM_NORMAL_SIZE);
        }
    }
}

