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
import org.github.logof.zxtiled.mapeditor.gui.panel.NewMapMainPanel;
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
    private NewMapMainPanel newMapMainPanel;

    public NewMapDialog(JFrame parent) {
        super(parent, DIALOG_TITLE);
        setBounds(new Rectangle(0, 0, 800, 600));
        setMinimumSize(new Dimension(800, 600));
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    @Override
    protected void initComponent() {
        new NewMapMainPanel(this, this);
    }

    public TileMap create() {
        setVisible(true);
        return tileMap;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(OK_BUTTON)) {
            int width = newMapMainPanel.getSizePanels().getMapScreenSizePanel().getMapWidthSpinner().intValue();
            int height = newMapMainPanel.getSizePanels().getMapScreenSizePanel().getMapHeightSpinner().intValue();

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
