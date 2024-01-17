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
import javax.swing.*;

/**
 * Closes the currently opened map.
 *
 * @version $Id$
 */
public class CloseMapAction extends AbstractFileAction {
    public CloseMapAction(MapEditor editor, SaveAction saveAction) {
        super(editor, saveAction,
                Resources.getString("action.map.close.name"),
                Resources.getString("action.map.close.tooltip"));

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
    }

    protected void doPerformAction() {
        editor.setCurrentTileMap(null);
    }
}
