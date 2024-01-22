package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.ObjectsLayer;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class CopyAction extends AbstractAction {
    private final MapEditor mapEditor;

    public CopyAction(MapEditor mapEditor) {
        super(Resources.getString("action.copy.name"));
        this.mapEditor = mapEditor;
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control C"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.copy.tooltip"));
    }

    public void actionPerformed(ActionEvent evt) {
        if (mapEditor.getCurrentTileMap() != null && mapEditor.getMarqueeSelection() != null) {
            MapLayer mapLayer = mapEditor.getCurrentLayer();
            if (mapLayer instanceof TileLayer) {
                mapEditor.setClipboardLayer(new TileLayer(
                        mapEditor.getMarqueeSelection().getSelectedAreaBounds(),
                        mapLayer.getTileWidth(),
                        mapLayer.getTileHeight()));
            } else if (mapEditor.getCurrentLayer() instanceof ObjectsLayer) {
                mapEditor.setClipboardLayer(new ObjectsLayer(mapEditor.getMarqueeSelection().getSelectedAreaBounds()));
            }
            mapEditor.getClipboardLayer().maskedCopyFrom(
                    mapEditor.getCurrentLayer(),
                    mapEditor.getMarqueeSelection().getSelectedArea());
        }
    }
}
