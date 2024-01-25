/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package org.github.logof.zxtiled.mapeditor.dialogs;

import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.ui.VerticalStaticJPanel;
import org.github.logof.zxtiled.mapeditor.undo.ChangeObjectEdit;
import org.github.logof.zxtiled.util.TiledConfiguration;
import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;

/**
 * A dialog for editing the name, type, size and properties of an object.
 *
 * @version $Id$
 */
public class ObjectDialog extends PropertiesDialog {
    /* LANGUAGE PACK */
    private static final String DIALOG_TITLE = Resources.getString("dialog.object.title");
    private static final String NAME_LABEL = Resources.getString("dialog.object.name.label");
    private static final String TYPE_LABEL = Resources.getString("dialog.object.type.label");
    private static final String IMAGE_LABEL = Resources.getString("dialog.object.image.label");
    private static final String UNTITLED_OBJECT = Resources.getString("general.object.object");
    private static final String BROWSE_BUTTON = Resources.getString("general.button.browse");
    private static String path;
    private final MapObject object;
    private JTextField objectName, objectType;
    private JTextField objectImageSource;


    public ObjectDialog(JFrame parent, MapObject object, UndoableEditSupport undoSupport) {
        super(parent, object.getProperties(), undoSupport);
        this.object = object;
        setTitle(DIALOG_TITLE);
        pack();
        setLocationRelativeTo(parent);
    }

    public void init() {
        super.init();
        JLabel nameLabel = new JLabel(NAME_LABEL);
        JLabel typeLabel = new JLabel(TYPE_LABEL);
        JLabel imageLabel = new JLabel(IMAGE_LABEL);

        objectName = new JTextField(UNTITLED_OBJECT);
        objectType = new JTextField();
        objectImageSource = new JTextField();

        final JButton browseButton = getBrowseButton();

        // Combine browse button and image source text field
        JPanel imageSourcePanel = new JPanel(new GridBagLayout());
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.gridx = 0;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 1;
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        imageSourcePanel.add(objectImageSource, bagConstraints);
        bagConstraints.gridx = 1;
        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.NONE;
        bagConstraints.insets = new Insets(0, 5, 0, 0);
        imageSourcePanel.add(browseButton, bagConstraints);

        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new GridBagLayout());
        miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        bagConstraints = new GridBagConstraints();
        bagConstraints.anchor = GridBagConstraints.EAST;
        bagConstraints.gridx = 0;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.NONE;
        bagConstraints.insets = new Insets(5, 0, 0, 5);
        miscPropPanel.add(nameLabel, bagConstraints);
        bagConstraints.gridy = 1;
        miscPropPanel.add(typeLabel, bagConstraints);
        bagConstraints.gridy = 2;
        miscPropPanel.add(imageLabel, bagConstraints);
        bagConstraints.insets = new Insets(5, 0, 0, 0);
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        bagConstraints.gridx = 1;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 1;
        miscPropPanel.add(objectName, bagConstraints);
        bagConstraints.gridy = 1;
        miscPropPanel.add(objectType, bagConstraints);
        bagConstraints.gridy = 2;
        miscPropPanel.add(imageSourcePanel, bagConstraints);

        mainPanel.add(miscPropPanel, 0);
    }

    private JButton getBrowseButton() {
        final JButton browseButton = new JButton(BROWSE_BUTTON);
        browseButton.addActionListener(actionEvent -> {
            String startLocation = path;
            if (startLocation == null) {
                startLocation = TiledConfiguration.fileDialogStartLocation();
            }
            JFileChooser ch = new JFileChooser(startLocation);

            int ret = ch.showOpenDialog(ObjectDialog.this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                path = ch.getSelectedFile().getAbsolutePath();
                objectImageSource.setText(path);
            }
        });
        return browseButton;
    }

    public void updateInfo() {
        super.updateInfo();
        objectName.setText(object.getName());
        objectType.setText(object.getType());
        objectImageSource.setText(object.getImageSource());
    }

    protected UndoableEdit commit() {
        CompoundEdit ce = new CompoundEdit();
        UndoableEdit propertyEdit = super.commit();
        if (propertyEdit != null) {
            ce.addEdit(propertyEdit);
        }

        // Make sure the changes to the object can be undone
        ce.addEdit(new ChangeObjectEdit(object));

        object.setName(objectName.getText());
        object.setType(objectType.getText());
        object.setImageSource(objectImageSource.getText());

        ce.end();

        return ce;
    }
}
