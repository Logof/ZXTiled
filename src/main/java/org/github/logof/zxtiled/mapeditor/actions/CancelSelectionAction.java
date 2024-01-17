package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class CancelSelectionAction extends AbstractAction {
    private final MapEditor mapEditor;

    public CancelSelectionAction(MapEditor mapEditor) {
        super(Resources.getString("action.select.none.name"));
        this.mapEditor = mapEditor;
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control shift A"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.select.none.tooltip"));
    }

    public void actionPerformed(ActionEvent e) {
        if (mapEditor.getCurrentTileMap() != null) {
            if (mapEditor.getMarqueeSelection() != null) {
                mapEditor.getCurrentTileMap().removeLayerSpecial(mapEditor.getMarqueeSelection());
            }
            mapEditor.setMarqueeSelection(null);
        }
    }
}
