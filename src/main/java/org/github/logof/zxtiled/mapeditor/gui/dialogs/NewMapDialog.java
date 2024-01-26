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
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.AbstractDialog;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import org.github.logof.zxtiled.mapeditor.gui.panel.MapScreenSizePanel;
import org.github.logof.zxtiled.mapeditor.gui.panel.MiscPropertiesPanel;
import org.github.logof.zxtiled.mapeditor.gui.panel.TileBmpPathPanel;
import org.github.logof.zxtiled.util.TiledConfiguration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class NewMapDialog extends AbstractDialog implements ActionListener {
    private static final String DIALOG_TITLE = Resources.getString("dialog.newmap.title");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");

    private final Preferences preferences = TiledConfiguration.node("dialog/newmap");
    private TileMap tileMap;
    private MapScreenSizePanel mapScreenSizePanel;

    public NewMapDialog(JFrame parent) {
        super(parent, DIALOG_TITLE);
        pack();
        setResizable(true);
        setLocationRelativeTo(parent);
    }

    @Override
    protected void initComponent() {
        // Tile size fields
        mapScreenSizePanel = new MapScreenSizePanel(this);
        // Map type and name inputs
        MiscPropertiesPanel miscPropPanel = new MiscPropertiesPanel(this);
        JPanel buttonsPanel = createButtonsPanel();

        TileBmpPathPanel tileBmpPathPanel = new TileBmpPathPanel(this);

        // Putting two side panels next to each other
        JPanel sizePanels = new JPanel();
        sizePanels.setLayout(new BoxLayout(sizePanels, BoxLayout.LINE_AXIS));
        sizePanels.add(mapScreenSizePanel);
        sizePanels.add(Box.createRigidArea(new Dimension(5, 0)));
        sizePanels.add(tileBmpPathPanel);

        // Application panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(miscPropPanel);
        mainPanel.add(sizePanels);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(Box.createGlue());
        mainPanel.add(buttonsPanel);
        getContentPane().add(mainPanel);
    }

    private JPanel createButtonsPanel() {
        // OK and Cancel buttons
        JButton okButton = new JButton(OK_BUTTON);
        JButton cancelButton = new JButton(CANCEL_BUTTON);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);


        JPanel buttonsPanel = new VerticalStaticJPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(Box.createGlue());
        buttonsPanel.add(okButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(cancelButton);
        
        getRootPane().setDefaultButton(okButton);

        return buttonsPanel;
    }

    public TileMap create() {
        setVisible(true);
        return tileMap;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(OK_BUTTON)) {
            int width = mapScreenSizePanel.getMapWidthSpinner().intValue();
            int height = mapScreenSizePanel.getMapHeightSpinner().intValue();

            //int orientation = TileMap.MDO_ORTHOGONAL;

            tileMap = new TileMap(width * 15, height * 10);
            tileMap.setTileWidth(16);
            tileMap.setTileHeight(16);
            tileMap.addAllLayers();
            tileMap.setMapType(MapTypeEnum.MAP_SIDE_SCROLLED);
            //tileMap.setOrientation(orientation);

            // Save dialog options
            preferences.putInt("mapWidth", width);
            preferences.putInt("mapHeight", height);
            preferences.putInt("tileWidth", 16);
            preferences.putInt("tileHeight", 16);
        }
        dispose();
    }
}
