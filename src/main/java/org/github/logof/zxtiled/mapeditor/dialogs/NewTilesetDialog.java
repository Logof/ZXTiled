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

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.ui.VerticalStaticJPanel;
import org.github.logof.zxtiled.mapeditor.util.cutter.BasicTileCutter;
import org.github.logof.zxtiled.mapeditor.util.cutter.TileCutter;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;


//TODO перенести в создание карты
/**
 * A dialog for creating a new tileset.
 */
public class NewTilesetDialog extends JDialog implements ChangeListener {
    /* LANGUAGE PACK */
    private static final String DIALOG_TITLE = Resources.getString("dialog.newtileset.title");
    private static final String NAME_LABEL = Resources.getString("dialog.newtileset.name.label");
    private static final String TILESET_NAME_ERR = Resources.getString("dialog.newtileset.name.error");
    private static final String IMAGE_LABEL = Resources.getString("dialog.newtileset.image.label");
    private static final String UNTITLED_FILE = Resources.getString("general.file.untitled");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");
    private static final String BROWSE_BUTTON = Resources.getString("general.button.browse");
    private static final String FROM_TILESET_IMG_TITLE = Resources.getString("dialog.newtileset.fromtilesetimg.title");
    private static final String IMPORT_ERROR_MSG = Resources.getString("dialog.newtileset.import.error.message");
    private static final String PROPERTIES_TITLE = Resources.getString("dialog.properties.default.title");
    private static final String PROPERTIES_BUTTON = Resources.getString("dialog.newtileset.button.properties");
    private final MapLayer layer;
    private Tileset newTileset;
    private JTextField tilesetName;
    private JTextField tilebmpFile;
    private JLabel tilebmpFileLabel;
    //TODO 16 или 48 тайлоа
    private JComboBox<String> countUsedTilesBox;
    private JButton browseButton;
    private JButton propsButton;
    private String path;
    private final Properties defaultSetProperties;
    private final UndoableEditSupport undoSupport;

    /* -- */

    public NewTilesetDialog(JFrame parent, MapLayer layer, UndoableEditSupport undoSupport) {
        super(parent, DIALOG_TITLE, true);
        this.undoSupport = undoSupport;
        this.layer = layer;
        path = layer.getTileMap().getFilename();
        defaultSetProperties = new Properties();
        init();
        pack();
        setLocationRelativeTo(parent);
    }

    private void init() {
        // Create the primitives
        JLabel nameLabel = new JLabel(NAME_LABEL);

        tilebmpFileLabel = new JLabel(IMAGE_LABEL);

        tilesetName = new JTextField(UNTITLED_FILE);
        tilebmpFile = new JTextField(10);

        nameLabel.setLabelFor(tilesetName);
        tilebmpFileLabel.setLabelFor(tilebmpFile);

        countUsedTilesBox = new JComboBox<>(new String[]{"16", "48"});
        countUsedTilesBox.setEditable(false);

        JButton okButton = new JButton(OK_BUTTON);
        JButton cancelButton = new JButton(CANCEL_BUTTON);
        browseButton = new JButton(BROWSE_BUTTON);
        propsButton = new JButton(PROPERTIES_BUTTON);

        // Combine browse button and tile bitmap path text field

        JPanel tileBmpPathPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.gridx = 0;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 1;
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        tileBmpPathPanel.add(tilebmpFile, bagConstraints);
        bagConstraints.gridx = 1;
        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.NONE;
        bagConstraints.insets = new Insets(0, 5, 0, 0);
        tileBmpPathPanel.add(browseButton, bagConstraints);

        // Create the tile bitmap import setting panel
        JPanel tileBmpPanel = new VerticalStaticJPanel();
        tileBmpPanel.setLayout(new GridBagLayout());
        tileBmpPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(FROM_TILESET_IMG_TITLE),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));


        bagConstraints.gridx = 0;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 0;
        bagConstraints.anchor = GridBagConstraints.EAST;

        bagConstraints.gridwidth = 1;
        bagConstraints.insets = new Insets(5, 0, 0, 5);
        bagConstraints.fill = GridBagConstraints.NONE;
        tileBmpPanel.add(tilebmpFileLabel, bagConstraints);
        bagConstraints.gridx = 1;
        bagConstraints.gridy = 3;
        bagConstraints.weightx = 1;
        bagConstraints.insets = new Insets(5, 0, 0, 0);
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        bagConstraints.gridwidth = 3;
        tileBmpPanel.add(tileBmpPathPanel, bagConstraints);

        bagConstraints.gridx = 2;
        bagConstraints.gridy = 4;
        bagConstraints.gridwidth = 1;
        bagConstraints.weightx = 0;
        bagConstraints.insets = new Insets(5, 5, 0, 0);


        // OK and Cancel buttons
        JPanel buttons = new VerticalStaticJPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(okButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(cancelButton);

        // Top part of form
        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new GridBagLayout());
        miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        bagConstraints.gridx = 0;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.NONE;
        bagConstraints.insets = new Insets(5, 0, 0, 5);
        miscPropPanel.add(nameLabel, bagConstraints);
        bagConstraints.insets = new Insets(5, 0, 0, 0);
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        bagConstraints.gridx = 1;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 1;
        miscPropPanel.add(tilesetName, bagConstraints);
        bagConstraints.gridy = 1;
        miscPropPanel.add(propsButton, bagConstraints);

        // Application panel

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(miscPropPanel);
        mainPanel.add(tileBmpPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(Box.createGlue());
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(okButton);

        // Attach the behaviour
        okButton.addActionListener(actionEvent -> createSetAndDispose());
        cancelButton.addActionListener(actionEvent -> dispose());

        browseButton.addActionListener(actionEvent -> {
            JFileChooser ch = new JFileChooser(path);

            int ret = ch.showOpenDialog(NewTilesetDialog.this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                path = ch.getSelectedFile().getAbsolutePath();
                tilebmpFile.setText(path);
            }
        });

        propsButton.addActionListener(actionEvent -> {
            PropertiesDialog lpd =
                    new PropertiesDialog(null, defaultSetProperties, undoSupport);
            lpd.setTitle(PROPERTIES_TITLE);
            lpd.getProps();
        });
    }

    public Tileset create() {
        setVisible(true);
        return newTileset;
    }

    public TileCutter getCutter() {
        return new BasicTileCutter();
    }

    private void createSetAndDispose() {
        Vector<Tileset> tilesets = layer.getTileMap().getTilesets();
        for (Tileset tileset : tilesets) {
            if (tileset.getName().compareTo(tilesetName.getText()) == 0) {
                newTileset = null;
                JOptionPane.showMessageDialog(this, TILESET_NAME_ERR,
                        IMPORT_ERROR_MSG, JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        newTileset = new Tileset();
        newTileset.setName(tilesetName.getText());
        newTileset.setDefaultProperties(defaultSetProperties);

        final String file = tilebmpFile.getText();
        try {
            newTileset.importTileBitmap(file, getCutter());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                    IMPORT_ERROR_MSG, JOptionPane.WARNING_MESSAGE);
        }

        dispose();
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
    }
}
