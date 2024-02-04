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

import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.core.MapTypeEnum;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.enums.EnemyEnum;
import org.github.logof.zxtiled.mapeditor.gui.IntegerSpinner;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
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
    private static final String OBJECT_SPEED = Resources.getString("dialog.object.speed.label");
    private static final String UNTITLED_OBJECT = Resources.getString("general.object.object");
    private static final String BROWSE_BUTTON = Resources.getString("general.button.browse");
    private static String path;
    private final MapObject object;
    private JTextField objectName;
    private JComboBox<EnemyEnum> objectType;
    private JTextField objectImageSource;

    private IntegerSpinner speedValue;
    private IntegerSpinner deltaX;
    private IntegerSpinner deltaY;


    public ObjectDialog(JFrame parent, MapObject object, UndoableEditSupport undoSupport) {
        super(parent, object.getProperties(), undoSupport, false);
        this.object = object;
        init();
        setTitle(DIALOG_TITLE);
        pack();
        setLocationRelativeTo(parent);
    }

    public void init() {
        super.init();
        objectName = new JTextField(UNTITLED_OBJECT);
        objectType = new JComboBox<>(EnemyEnum.getValuesByMapType(MapTypeEnum.MAP_SIDE_SCROLLED));
        objectType.addActionListener(e -> object.setType((EnemyEnum) ((JComboBox<?>) e.getSource()).getSelectedItem()));
        objectImageSource = new JTextField();
        mainPanel.add(createMiscPropPanel(), 0);
    }

    private JPanel createImageSourcePanel() {
        JPanel imageSourcePanel = new JPanel(new GridBagLayout());
        GridBagConstraints imageConstraints = new GridBagConstraints();
        imageConstraints.gridx = 0;
        imageConstraints.gridy = 0;
        imageConstraints.fill = GridBagConstraints.HORIZONTAL;
        imageSourcePanel.add(objectImageSource, imageConstraints);
        imageConstraints.gridx = 1;
        imageConstraints.weightx = 0;
        imageConstraints.fill = GridBagConstraints.NONE;
        imageConstraints.insets = new Insets(0, 5, 0, 0);
        imageSourcePanel.add(getBrowseButton(), imageConstraints);

        return imageSourcePanel;
    }

    private JPanel createMiscPropPanel() {
        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new GridBagLayout());
        miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        GridBagConstraints miscConstraints = new GridBagConstraints();
        miscConstraints.anchor = GridBagConstraints.LINE_END;
        miscConstraints.gridx = 0;
        miscConstraints.gridy = 0;
        miscConstraints.weightx = 0;
        miscConstraints.fill = GridBagConstraints.NONE;
        miscConstraints.insets = new Insets(5, 0, 0, 5);
        miscPropPanel.add(new JLabel(NAME_LABEL), miscConstraints);
        miscConstraints.gridy = 1;
        miscConstraints.weighty = 4;
        miscPropPanel.add(new JLabel(TYPE_LABEL), miscConstraints);
        miscConstraints.gridy = 2;
        miscConstraints.weighty = 4;
        miscPropPanel.add(new JLabel(OBJECT_SPEED), miscConstraints);
        miscConstraints.gridy = 3;
        miscPropPanel.add(new JLabel("Смещение:"), miscConstraints);
        miscConstraints.gridy = 4;
        miscPropPanel.add(new JLabel("x"), miscConstraints);
        miscConstraints.gridy = 5;
        miscPropPanel.add(new JLabel("y"), miscConstraints);
        miscConstraints.gridy = 6;
        miscConstraints.gridx = 0;
        miscPropPanel.add(new JLabel(IMAGE_LABEL), miscConstraints);
        miscConstraints.insets = new Insets(5, 0, 0, 0);
        miscConstraints.fill = GridBagConstraints.HORIZONTAL;
        miscConstraints.gridx = 1;
        miscConstraints.gridy = 0;
        miscConstraints.weightx = 1;
        miscPropPanel.add(objectName, miscConstraints);
        miscConstraints.gridy = 1;
        miscPropPanel.add(objectType, miscConstraints);
        miscConstraints.gridy = 2;

        //TODO fixme: 1, 2, 4 2^0, 2^1, 2^2
        speedValue = new IntegerSpinner(0, 0, 4);
        miscPropPanel.add(speedValue, miscConstraints);

        miscConstraints.gridy = 4;
        deltaX = new IntegerSpinner(object.getCoordinateXAt(), 0, Constants.SCREEN_WIDTH);
        miscPropPanel.add(deltaX, miscConstraints);

        miscConstraints.gridy = 5;
        deltaY = new IntegerSpinner(object.getCoordinateYAt(), 0, Constants.SCREEN_HEIGHT);
        miscPropPanel.add(deltaY, miscConstraints);

        miscConstraints.gridy = 6;
        miscPropPanel.add(new JTextField(), miscConstraints);

        return miscPropPanel;
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
        objectType.setSelectedItem(object.getType());
        objectImageSource.setText(object.getImageSource());
        speedValue.setValue(object.getSpeed());
        deltaX.setValue(object.getPath().width);
        deltaY.setValue(object.getPath().height);

    }

    protected UndoableEdit commit() {
        CompoundEdit compoundEdit = new CompoundEdit();
        UndoableEdit propertyEdit = super.commit();
        if (propertyEdit != null) {
            compoundEdit.addEdit(propertyEdit);
        }

        // Make sure the changes to the object can be undone
        compoundEdit.addEdit(new ChangeObjectEdit(object));

        object.setName(objectName.getText());
        objectType.setSelectedItem(object.getType());
        object.setImageSource(objectImageSource.getText());
        object.setSpeed((int) speedValue.getValue());
        object.setPath(new Rectangle((int) deltaX.getValue(), (int) deltaY.getValue()));
        compoundEdit.end();

        return compoundEdit;
    }
}
