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

import org.github.logof.zxtiled.core.Map;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.dialogs.NewMapDialog;
import javax.swing.*;

/**
 * Creates a new map.
 */
public class NewMapAction extends AbstractFileAction
{
    public NewMapAction(MapEditor editor, SaveAction saveAction) {
        super(editor, saveAction,
              Resources.getString("action.map.new.name"),
              Resources.getString("action.map.new.tooltip"));

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
    }

    protected void doPerformAction() {
        NewMapDialog nmd = new NewMapDialog((JFrame)editor.getAppFrame());
        Map newMap = nmd.create();
        if (newMap != null) {
            editor.setCurrentMap(newMap);
        }
    }
}
