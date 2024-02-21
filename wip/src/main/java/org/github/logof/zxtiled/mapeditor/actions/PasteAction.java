package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.undo.MapLayerStateEdit;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class PasteAction extends AbstractAction {
    private final MapEditor mapEditor;

    public PasteAction(MapEditor mapEditor) {
        super(Resources.getString("action.paste.name"));
        this.mapEditor = mapEditor;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
        putValue(SHORT_DESCRIPTION, Resources.getString("action.paste.tooltip"));
    }

    public void actionPerformed(ActionEvent evt) {
        if (mapEditor.getCurrentTileMap() != null && mapEditor.getClipboardLayer() != null) {
            Vector<MapLayer> layersBefore = mapEditor.getCurrentTileMap().getLayerVector();
            MapLayer mapLayer = MapEditor.createLayerCopy(mapEditor.getClipboardLayer());
            mapLayer.setName(Resources.getString("general.layer.layer") + " " + mapEditor.getCurrentTileMap()
                                                                                         .getTotalLayers());
            mapEditor.getCurrentTileMap().addLayer(mapLayer);
            mapEditor.getUndoSupport().postEdit(
                    new MapLayerStateEdit(mapEditor.getCurrentTileMap(), layersBefore,
                            new Vector<>(mapEditor.getCurrentTileMap().getLayerVector()),
                            "Paste Selection"));
        }
    }
}

