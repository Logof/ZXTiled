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

import org.github.logof.zxtiled.core.Map;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.widget.IntegerSpinner;
import org.github.logof.zxtiled.mapeditor.widget.VerticalStaticJPanel;
import org.github.logof.zxtiled.util.TiledConfiguration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class NewMapDialog extends JDialog implements ActionListener {
    private static final String DIALOG_TITLE = Resources.getString("dialog.newmap.title");
    private static final String MAP_SIZE_TITLE = Resources.getString("dialog.newmap.mapsize.title");
    private static final String TILE_SIZE_TITLE = Resources.getString("dialog.newmap.tilesize.title");
    private static final String WIDTH_LABEL = Resources.getString("dialog.newmap.width.label");
    private static final String HEIGHT_LABEL = Resources.getString("dialog.newmap.height.label");
    private static final String MAP_TYPE_LABEL = Resources.getString("dialog.newmap.maptype.label");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");
    private static final String ORTHOGONAL_MAP_TYPE = Resources.getString("general.maptype.orthogonal");
    private final Preferences preferences = TiledConfiguration.node("dialog/newmap");
    private Map newMap;
    private IntegerSpinner mapWidth;
    private IntegerSpinner mapHeight;
    private IntegerSpinner tileWidth;
    private IntegerSpinner tileHeight;

    public NewMapDialog(JFrame parent) {
        super(parent, DIALOG_TITLE, true);
        init();
        pack();
        setResizable(true);
        setLocationRelativeTo(parent);
    }

    private void init() {
        // Load dialog defaults
        int defaultMapWidth = preferences.getInt("mapWidth", 64);
        int defaultMapHeight = preferences.getInt("mapHeight", 64);
        int defaultTileWidth = 16;
        int defaultTileHeight = 16;

        // Create the primitives
        mapWidth = new IntegerSpinner(defaultMapWidth, 1);
        mapHeight = new IntegerSpinner(defaultMapHeight, 1);
        tileWidth = new IntegerSpinner(defaultTileWidth, 1);
        tileHeight = new IntegerSpinner(defaultTileHeight, 1);

        // Map size fields
        JPanel mapScreenSize = new VerticalStaticJPanel();
        mapScreenSize.setLayout(new GridBagLayout());
        mapScreenSize.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(MAP_SIZE_TITLE),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        mapScreenSize.add(new JLabel(WIDTH_LABEL), gridBagConstraints);
        gridBagConstraints.gridy = 1;
        mapScreenSize.add(new JLabel(HEIGHT_LABEL), gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        mapScreenSize.add(mapWidth, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        mapScreenSize.add(mapHeight, gridBagConstraints);

        // Tile size fields
        JPanel tileSize = new VerticalStaticJPanel();
        tileSize.setLayout(new GridBagLayout());
        tileSize.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(TILE_SIZE_TITLE),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        tileSize.add(new JLabel(WIDTH_LABEL), gridBagConstraints);
        gridBagConstraints.gridy = 1;
        tileSize.add(new JLabel(HEIGHT_LABEL), gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        tileSize.add(tileWidth, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        tileSize.add(tileHeight, gridBagConstraints);

        // OK and Cancel buttons
        JButton okButton = new JButton(OK_BUTTON);
        JButton cancelButton = new JButton(CANCEL_BUTTON);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JPanel buttons = new VerticalStaticJPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(okButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(cancelButton);

        // Map type and name inputs
        JComboBox<String> mapTypeChooser = new JComboBox<>();
        mapTypeChooser.addItem(ORTHOGONAL_MAP_TYPE);

        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new GridBagLayout());
        miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        miscPropPanel.add(new JLabel(MAP_TYPE_LABEL), gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        miscPropPanel.add(mapTypeChooser, gridBagConstraints);

        // Putting two size panels next to eachother
        JPanel sizePanels = new JPanel();
        sizePanels.setLayout(new BoxLayout(sizePanels, BoxLayout.X_AXIS));
        sizePanels.add(mapScreenSize);
        sizePanels.add(Box.createRigidArea(new Dimension(5, 0)));
        sizePanels.add(tileSize);

        // Application panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(miscPropPanel);
        mainPanel.add(sizePanels);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(Box.createGlue());
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(okButton);
    }

    public Map create() {
        setVisible(true);
        return newMap;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(OK_BUTTON)) {
            int width = mapWidth.intValue();
            int height = mapHeight.intValue();
            int orientation = Map.MDO_ORTHOGONAL;

            newMap = new Map(width * 15, height * 10);
            newMap.setTileWidth(16);
            newMap.setTileHeight(16);
            newMap.addLayer();
            newMap.setOrientation(orientation);

            // Save dialog options

            preferences.putInt("mapWidth", mapWidth.intValue());
            preferences.putInt("mapHeight", mapHeight.intValue());
            preferences.putInt("tileWidth", 16);
            preferences.putInt("tileHeight", 16);
        }
        dispose();
    }
}
