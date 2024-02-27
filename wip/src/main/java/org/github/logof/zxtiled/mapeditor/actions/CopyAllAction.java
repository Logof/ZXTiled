package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class CopyAllAction extends AbstractAction {
    private final MapEditor mapEditor;

    public CopyAllAction(MapEditor mapEditor) {
        super(Resources.getString("action.copyall.name"));
        this.mapEditor = mapEditor;
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("shift control C"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.copyall.tooltip"));
    }

    public void actionPerformed(ActionEvent evt) {
        //FIXME: only works for TileLayers
        if (mapEditor.getCurrentTileMap() != null && mapEditor.getMarqueeSelection() != null) {
            MapLayer mapLayer = mapEditor.getCurrentLayer();
            mapEditor.setClipboardLayer(new TileLayer(mapEditor.getMarqueeSelection().getSelectedAreaBounds()
            ));

            if (mapEditor.getCurrentTileMap().getTileLayer() != null) {
                mapEditor.getClipboardLayer()
                         .maskedMergeOnto(mapEditor.getCurrentTileMap().getTileLayer(),
                                 mapEditor.getMarqueeSelection().getSelectedArea());
            }

        }
    }
}
