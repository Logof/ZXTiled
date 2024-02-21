/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.LayerPropertiesDialog;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.PropertiesDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author upachler
 */
public class ShowLayerPropertiesAction extends AbstractAction {

    private static final String ACTION_NAME = Resources.getString("menu.layer.properties");
    private static final String ACTION_TOOLTIP = Resources.getString("menu.layer.properties.tooltip");

    private final MapEditor editor;

    public ShowLayerPropertiesAction(MapEditor editor) {
        super(ACTION_NAME);
        putValue(SHORT_DESCRIPTION, ACTION_TOOLTIP);
        this.editor = editor;
    }

    public void actionPerformed(ActionEvent e) {
        MapLayer layer = editor.getCurrentLayer();
        PropertiesDialog lpd =
                new LayerPropertiesDialog(editor.getAppFrame(), layer, editor.getUndoSupport());
        lpd.setTitle(layer.getName() + " " + Resources.getString("dialog.properties.title"));
        lpd.getProps();
        editor.updateLayerOperations();
        editor.getMapView().repaint();
    }
}
