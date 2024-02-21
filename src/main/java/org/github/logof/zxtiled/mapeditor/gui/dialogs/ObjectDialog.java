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

package org.github.logof.zxtiled.mapeditor.gui.dialogs;

import org.github.logof.zxtiled.core.MapTypeEnum;
import org.github.logof.zxtiled.core.objects.HotspotObject;
import org.github.logof.zxtiled.core.objects.MapObject;
import org.github.logof.zxtiled.core.objects.MovingObject;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.enums.HotspotEnum;
import org.github.logof.zxtiled.mapeditor.enums.MovingObjectTypeEnum;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import org.github.logof.zxtiled.mapeditor.undo.ChangeObjectEdit;
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
    /* LANGUAGE PACK */
    private static final String DIALOG_TITLE = Resources.getString("dialog.object.title");
    private static final String NAME_LABEL = Resources.getString("dialog.object.name.label");
    private static final String TYPE_LABEL = Resources.getString("dialog.object.type.label");
    private static final String IMAGE_LABEL = Resources.getString("dialog.object.image.label");
    private static final String OBJECT_SPEED = Resources.getString("dialog.object.speed.label");
    private static final String UNTITLED_OBJECT = Resources.getString("general.object.object");
    private static final String BROWSE_BUTTON = Resources.getString("general.button.browse");
    private final MapObject mapObject;
    private JTextField objectName;
    private JComboBox<?> objectType;
    private JTextField objectImageSource;

    public ObjectDialog(JFrame parent, MapObject mapObject, UndoableEditSupport undoSupport) {
        super(parent, mapObject.getProperties(), undoSupport, false);
        this.mapObject = mapObject;
        setTitle(DIALOG_TITLE);
        init();
        pack();
        setLocationRelativeTo(parent);
    }

    public void init() {
        super.init();

        // Label's
        JLabel nameLabel = new JLabel(NAME_LABEL);
        JLabel typeLabel = new JLabel(TYPE_LABEL);
        JLabel xLabel = new JLabel("X");
        JLabel yLabel = new JLabel("Y");
        JLabel speedLabel = new JLabel(OBJECT_SPEED);

        JLabel imageLabel = new JLabel(IMAGE_LABEL);

        // Input field's
        objectName = new JTextField(UNTITLED_OBJECT);

        if (mapObject instanceof MovingObject) {
            objectType = new JComboBox<>(MovingObjectTypeEnum.getValuesByMapType(MapTypeEnum.MAP_SIDE_SCROLLED));
            objectType.addActionListener(e -> ((MovingObject) mapObject)
                    .setType((MovingObjectTypeEnum) ((JComboBox<?>) e.getSource()).getSelectedItem()));
        }

        if (mapObject instanceof HotspotObject) {
            objectType = new JComboBox<>(HotspotEnum.getValuesByMapType(MapTypeEnum.MAP_SIDE_SCROLLED));
            objectType.addActionListener(e -> ((HotspotObject) mapObject)
                    .setType((HotspotEnum) ((JComboBox<?>) e.getSource()).getSelectedItem()));
        }

        objectImageSource = new JTextField();

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

        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new GridBagLayout());
        miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        bagConstraints = new GridBagConstraints();
        bagConstraints.anchor = GridBagConstraints.LINE_END;
        bagConstraints.gridx = 0;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.NONE;
        bagConstraints.insets = new Insets(5, 0, 0, 5);
        miscPropPanel.add(nameLabel, bagConstraints);
        bagConstraints.gridy = 1;
        bagConstraints.weighty = 4;
        miscPropPanel.add(typeLabel, bagConstraints);
        bagConstraints.gridy = 2;
        bagConstraints.weighty = 4;
        miscPropPanel.add(speedLabel, bagConstraints);

        JPanel movingCoordinates = new JPanel();
        movingCoordinates.setLayout(new GridBagLayout());
        GridBagConstraints movingBagConstraints = new GridBagConstraints();
        movingBagConstraints.gridx = 0;
        movingBagConstraints.gridy = 0;
        movingCoordinates.add(xLabel, movingBagConstraints);
        movingBagConstraints.gridx = 0;
        movingBagConstraints.gridy = 1;
        movingCoordinates.add(yLabel, movingBagConstraints);
        movingBagConstraints.gridx = 1;
        movingBagConstraints.gridy = 0;
        movingBagConstraints.weightx = 1;
        movingCoordinates.add(new JTextField(), movingBagConstraints);
        movingBagConstraints.gridy = 1;
        movingBagConstraints.gridx = 1;
        movingBagConstraints.weightx = 1;
        movingCoordinates.add(new JTextField(), movingBagConstraints);

        bagConstraints.gridy = 3;
        miscPropPanel.add(movingCoordinates, bagConstraints);

        bagConstraints.insets = new Insets(5, 0, 0, 0);
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        bagConstraints.gridx = 1;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 1;
        miscPropPanel.add(objectName, bagConstraints);
        bagConstraints.gridy = 1;
        miscPropPanel.add(objectType, bagConstraints);
        bagConstraints.gridx = 4;
        miscPropPanel.add(new JTextField(), bagConstraints);

        mainPanel.add(miscPropPanel, 0);
    }

    public void updateInfo() {
        super.updateInfo();
        objectName.setText(mapObject.getName());
        //objectType.setSelectedItem(object.getType());
        objectImageSource.setText(mapObject.getImageSource());
    }

    protected UndoableEdit commit() {
        CompoundEdit ce = new CompoundEdit();
        UndoableEdit propertyEdit = super.commit();
        if (propertyEdit != null) {
            ce.addEdit(propertyEdit);
        }

        // Make sure the changes to the object can be undone
        ce.addEdit(new ChangeObjectEdit(mapObject));

        mapObject.setName(objectName.getText());
        //objectType.setSelectedItem(object.getType());
        mapObject.setImageSource(objectImageSource.getText());

        ce.end();

        return ce;
    }
}
