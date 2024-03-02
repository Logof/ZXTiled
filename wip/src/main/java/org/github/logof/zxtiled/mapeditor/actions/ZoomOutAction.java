package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.github.logof.zxtiled.view.MapView.ZOOM_NORMAL_SIZE;

public class ZoomOutAction extends AbstractAction {
    private final MapEditor mapEditor;

    public ZoomOutAction(MapEditor mapEditor) {
        super(Resources.getString("action.zoom.out.name"));
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control MINUS"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.zoom.out.tooltip"));
        putValue(SMALL_ICON, Resources.getIcon("icon/gnome-zoom-out.png"));
        this.mapEditor = mapEditor;
    }

    public void actionPerformed(ActionEvent evt) {
        if (mapEditor.getCurrentTileMap() != null) {
            MapEditorAction.zoomInAction.setEnabled(true);
            if (!mapEditor.getMapView().zoomOut()) {
                setEnabled(false);
            }
            MapEditorAction.zoomNormalAction.setEnabled(mapEditor.getMapView().getZoomLevel() != ZOOM_NORMAL_SIZE);
        }
    }
}