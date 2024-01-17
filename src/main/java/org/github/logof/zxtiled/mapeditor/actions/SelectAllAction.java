package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class SelectAllAction extends AbstractAction {
    private final MapEditor mapEditor;

    public SelectAllAction(MapEditor mapEditor) {
        super(Resources.getString("action.select.all.name"));
        this.mapEditor = mapEditor;
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control A"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.select.all.tooltip"));
    }

    public void actionPerformed(ActionEvent e) {
        if (mapEditor.getCurrentTileMap() != null) {
            if (mapEditor.getMarqueeSelection() != null) {
                mapEditor.getCurrentTileMap().removeLayerSpecial(mapEditor.getMarqueeSelection());
            }
            mapEditor.setMarqueeSelection(new SelectionLayer(mapEditor.getCurrentLayer()));
            mapEditor.getMarqueeSelection().selectRegion(mapEditor.getMarqueeSelection().getBounds());
            mapEditor.getCurrentTileMap().addLayerSpecial(mapEditor.getMarqueeSelection());
        }
    }
}

