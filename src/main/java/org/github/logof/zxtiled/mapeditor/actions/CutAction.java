package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.ObjectsLayer;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Area;

public class CutAction extends AbstractAction {
    private final MapEditor mapEditor;

    public CutAction(MapEditor mapEditor) {
        super(Resources.getString("action.cut.name"));
        this.mapEditor = mapEditor;
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control X"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.cut.tooltip"));
    }

    public void actionPerformed(ActionEvent evt) {
        if (mapEditor.getCurrentTileMap() != null && mapEditor.getMarqueeSelection() != null) {
            MapLayer mapLayer = mapEditor.getCurrentLayer();

            if (mapEditor.getCurrentLayer() instanceof TileLayer) {
                mapEditor.setClipboardLayer(new TileLayer(mapEditor.getMarqueeSelection().getSelectedAreaBounds(),
                        mapLayer.getTileWidth(), mapLayer.getTileHeight()));
            } else if (mapEditor.getCurrentLayer() instanceof ObjectsLayer) {
                mapEditor.setClipboardLayer(new ObjectsLayer(mapEditor.getMarqueeSelection().getSelectedAreaBounds()));
            }
            mapEditor.getClipboardLayer().maskedCopyFrom(mapLayer, mapEditor.getMarqueeSelection().getSelectedArea());

            Rectangle area = mapEditor.getMarqueeSelection().getSelectedAreaBounds();
            Area mask = mapEditor.getMarqueeSelection().getSelectedArea();
            if (mapLayer instanceof TileLayer) {
                TileLayer tl = (TileLayer) mapLayer;
                for (int i = area.y; i < area.height + area.y; i++) {
                    for (int j = area.x; j < area.width + area.x; j++) {
                        if (mask.contains(j, i)) {
                            tl.setTileAt(j, i, null);
                        }
                    }
                }
            }
            mapEditor.getMapView().repaintRegion(mapLayer, area);
        }
    }
}