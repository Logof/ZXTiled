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
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.AbstractDialog;
import org.github.logof.zxtiled.mapeditor.gui.IntegerSpinner;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import org.github.logof.zxtiled.util.ZXTiledConfiguration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class NewMapDialog extends AbstractDialog implements ActionListener {
    private static final String DIALOG_TITLE = Resources.getString("dialog.newmap.title");
    private static final String MAP_SIZE_TITLE = Resources.getString("dialog.newmap.mapsize.title");
    private static final String TILE_SIZE_TITLE = Resources.getString("dialog.newmap.tilesize.title");
    private static final String WIDTH_LABEL = Resources.getString("dialog.newmap.width.label");
    private static final String HEIGHT_LABEL = Resources.getString("dialog.newmap.height.label");
    private static final String MAP_TYPE_LABEL = Resources.getString("dialog.newmap.maptype.label");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");
    private static final String MAP_TYPE_SIDE_SCROLLED = Resources.getString("general.map.type.side-scrolled");
    private static final String MAP_TYPE_TOP_DOWN = Resources.getString("general.map.type.topdown");
    private final Preferences preferences = ZXTiledConfiguration.node("dialog/newmap");
    private TileMap tileMap;

    private IntegerSpinner mapWidth;
    private IntegerSpinner mapHeight;

    public NewMapDialog(JFrame parent) {
        super(parent, DIALOG_TITLE);
        initComponent();
        setBounds(new Rectangle(0, 0, 800, 600));
        setMinimumSize(new Dimension(800, 600));
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    @Override
    protected void initComponent() {
        // Load dialog defaults
        int defaultMapWidth = 1;
        int defaultMapHeight = 1;

        // Create the primitives
        mapWidth = new IntegerSpinner(defaultMapWidth, 1);
        mapHeight = new IntegerSpinner(defaultMapHeight, 1);

        // Map size fields
        JPanel mapScreenSize = new VerticalStaticJPanel();
        mapScreenSize.setLayout(new GridBagLayout());
        mapScreenSize.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(MAP_SIZE_TITLE),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.fill = GridBagConstraints.PAGE_END;
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
        mapTypeChooser.addItem(MAP_TYPE_SIDE_SCROLLED);
        mapTypeChooser.addItem(MAP_TYPE_TOP_DOWN);

        JPanel projectTypePropertyPanel = new VerticalStaticJPanel();
        projectTypePropertyPanel.setLayout(new GridBagLayout());
        projectTypePropertyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        projectTypePropertyPanel.add(new JLabel(MAP_TYPE_LABEL), gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        projectTypePropertyPanel.add(mapTypeChooser, gridBagConstraints);

        // Putting two size panels next to eachother
        JPanel sizePanels = new JPanel();
        sizePanels.setLayout(new BoxLayout(sizePanels, BoxLayout.X_AXIS));
        sizePanels.add(mapScreenSize);

        // Application panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(projectTypePropertyPanel);
        mainPanel.add(sizePanels);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(Box.createGlue());
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(okButton);
    }

    public TileMap create() {
        setVisible(true);
        return tileMap;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(OK_BUTTON)) {
            int width = mapWidth.intValue();
            int height = mapHeight.intValue();

            tileMap = new TileMap(width * Constants.SCREEN_WIDTH, height * Constants.SCREEN_HEIGHT);
            tileMap.addAllLayers();
            tileMap.setMapType(MapTypeEnum.MAP_SIDE_SCROLLED);
            //tileMap.setOrientation(orientation);

            // Save dialog options
            preferences.putInt("mapWidth", width);
            preferences.putInt("mapHeight", height);
            preferences.putInt("tileWidth", Constants.TILE_WIDTH);
            preferences.putInt("tileHeight", Constants.TILE_HEIGHT);
        }
        dispose();
    }
}
