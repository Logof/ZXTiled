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

package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.util.TiledFileFilter;
import org.github.logof.zxtiled.util.ZXTiledConfiguration;
import javax.swing.*;

/**
 * Opens the map open dialog.
 *
 * @version $Id$
 */
public class OpenMapAction extends AbstractFileAction {
    private static final String OPEN_ERROR_TITLE = Resources.getString("dialog.saveas.error.title");

    public OpenMapAction(MapEditor editor, SaveAction saveAction) {
        super(editor, saveAction,
                Resources.getString("action.map.open.name"),
                Resources.getString("action.map.open.tooltip"));

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
    }

    protected void doPerformAction() {
        // Start at the location of the most recently loaded map file
        String startLocation = ZXTiledConfiguration.fileDialogStartLocation();

        JFileChooser chooser = new JFileChooser(startLocation);

        chooser.addChoosableFileFilter(
                new TiledFileFilter(TiledFileFilter.FILTER_TMX));

        int ret = chooser.showOpenDialog(editor.getAppFrame());
        if (ret == JFileChooser.APPROVE_OPTION) {
            editor.loadMap(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}
