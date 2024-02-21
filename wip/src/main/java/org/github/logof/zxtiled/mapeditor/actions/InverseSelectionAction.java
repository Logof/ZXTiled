package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class InverseSelectionAction extends AbstractAction {
    private final MapEditor mapEditor;

    public InverseSelectionAction(MapEditor mapEditor) {
        super(Resources.getString("action.select.invert.name"));
        this.mapEditor = mapEditor;
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control I"));
        putValue(SHORT_DESCRIPTION,
                Resources.getString("action.select.invert.tooltip"));
    }

    public void actionPerformed(ActionEvent e) {
        if (mapEditor.getMarqueeSelection() != null) {
            mapEditor.getMarqueeSelection().invert();
            mapEditor.getMapView().repaint();
        }
    }
}
