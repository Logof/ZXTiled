package org.github.logof.zxtiled.mapeditor.listener;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MapEditorChangeListener implements ChangeListener {
    private final MapEditor mapEditor;

    public MapEditorChangeListener(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        JViewport mapViewport = mapEditor.getMapScrollPane().getViewport();

        if (changeEvent.getSource() == mapEditor.getOpacitySlider()) {
            if (mapEditor.getCurrentTileMap() != null && mapEditor.getCurrentLayerIndex() >= 0) {
                MapLayer layer = mapEditor.getCurrentLayer();
                layer.setOpacity(mapEditor.getOpacitySlider().getValue() / 100.0f);
            }
        } else if (changeEvent.getSource() == mapViewport && mapEditor.getMapView() != null) {
            // Store the point in the middle for zooming purposes
            Rectangle viewRect = mapViewport.getViewRect();
            mapEditor.setRelativeMidX(Math.min(1, (viewRect.x + (float) viewRect.width / 2) / (float) mapEditor.getMapView()
                                                                                                               .getWidth()));
            mapEditor.setRelativeMidY(Math.min(1, (viewRect.y + (float) viewRect.height / 2) / (float) mapEditor.getMapView()
                                                                                                                .getHeight()));
        }
    }
}
