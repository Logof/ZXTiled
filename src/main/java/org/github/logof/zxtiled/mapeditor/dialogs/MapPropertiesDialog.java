/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.dialogs;

import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.ui.VerticalStaticJPanel;
import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;

/**
 * @author count
 */
public class MapPropertiesDialog extends PropertiesDialog {
    private static final String DIALOG_TITLE = Resources.getString("dialog.map.properties.title");

    public MapPropertiesDialog(JFrame parent, TileMap tileMap, UndoableEditSupport undoSupport) {
        super(parent, tileMap.getProperties(), undoSupport, false);
        init();
        setTitle(DIALOG_TITLE);
        pack();
        setLocationRelativeTo(parent);
    }

    public void init() {
        super.init();

        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new GridBagLayout());
        miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;

        mainPanel.add(miscPropPanel, 0);
    }

    public void updateInfo() {
        super.updateInfo();
    }

    protected UndoableEdit commit() {
        // Make sure the changes to the object can be undone

        UndoableEdit propertyEdit = super.commit();

        CompoundEdit compoundEdit = new CompoundEdit();
        if (propertyEdit != null) {
            compoundEdit.addEdit(propertyEdit);
        }
        compoundEdit.end();
        return compoundEdit;
    }
}
