package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.github.logof.zxtiled.view.MapView.ZOOM_NORMAL_SIZE;

public class ZoomInAction extends AbstractAction {
    private final MapEditor mapEditor;

    public ZoomInAction(MapEditor mapEditor) {
        super(Resources.getString("action.zoom.in.name"));
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control EQUALS"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.zoom.in.tooltip"));
        putValue(SMALL_ICON, Resources.getIcon("icon/gnome-zoom-in.png"));
        this.mapEditor = mapEditor;
    }

    public void actionPerformed(ActionEvent evt) {
        if (mapEditor.getCurrentTileMap() != null) {
            MapEditorAction.zoomOutAction.setEnabled(true);
            if (!mapEditor.getMapView().zoomIn()) {
                setEnabled(false);
            }
            MapEditorAction.zoomNormalAction.setEnabled(mapEditor.getMapView().getZoomLevel() != ZOOM_NORMAL_SIZE);
        }
    }
}
