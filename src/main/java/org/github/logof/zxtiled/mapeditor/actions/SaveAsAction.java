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

import lombok.Getter;
import org.github.logof.zxtiled.io.MapHelper;
import org.github.logof.zxtiled.io.MapWriter;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.util.ConfirmingFileChooser;
import org.github.logof.zxtiled.mapeditor.util.TiledFileFilter;
import org.github.logof.zxtiled.util.TiledConfiguration;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * A save action that always shows a file chooser.
 *
 * @version $Id$
 */
public class SaveAsAction extends AbstractAction {
    private static final String ACTION_NAME = Resources.getString("action.map.save.as.name");
    private static final String ACTION_TOOLTIP = Resources.getString("action.map.save.as.tooltip");
    private static final String SAVE_AS_ERROR_MESSAGE = Resources.getString("dialog.saveas.error.message");
    private static final String SAVE_AS_ERROR_TITLE = Resources.getString("dialog.saveas.error.title");
    protected MapEditor editor;
    @Getter
    private boolean savingCancelled;

    public SaveAsAction(MapEditor editor) {
        super(ACTION_NAME);
        putValue(SHORT_DESCRIPTION, ACTION_TOOLTIP);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
        this.editor = editor;
    }

    public void actionPerformed(ActionEvent e) {
        showFileChooser();
    }

    /**
     * Shows the confirming file chooser and proceeds with saving the map when
     * a filename was approved.
     */
    protected void showFileChooser() {
        // Start at the location of the most recently loaded map file
        String startLocation =
                TiledConfiguration.node("recent").get("file0", null);

        TiledFileFilter byExtensionFilter =
                new TiledFileFilter(TiledFileFilter.FILTER_EXT);
        TiledFileFilter tmxFilter =
                new TiledFileFilter(TiledFileFilter.FILTER_TMX);

        JFileChooser chooser = new ConfirmingFileChooser(startLocation);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(byExtensionFilter);
        chooser.addChoosableFileFilter(tmxFilter);

        MapWriter[] writers = editor.getPluginLoader().getWriters();
        for (MapWriter writer : writers) {
            try {
                chooser.addChoosableFileFilter(new TiledFileFilter(writer));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chooser.setFileFilter(byExtensionFilter);

        int result = chooser.showSaveDialog(editor.getAppFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            savingCancelled = false;
            TiledFileFilter saver = (TiledFileFilter) chooser.getFileFilter();
            String selectedFile = chooser.getSelectedFile().getAbsolutePath();
            saveFile(saver, selectedFile);
        } else {
            savingCancelled = true;
        }
    }

    /**
     * Actually saves the map.
     *
     * @param saver    the file filter selected when the filename was chosen
     * @param filename the filename to save the map to
     */
    protected void saveFile(TiledFileFilter saver, String filename) {
        try {
            if (saver.getType() == TiledFileFilter.FILTER_EXT) {
                MapHelper.saveMap(editor.getCurrentTileMap(), filename);
            } else {
                MapHelper.saveMap(editor.getCurrentTileMap(), saver.getPlugin(), filename);
            }

            // The file was saved successfully, update some things.
            // todo: this could probably be done a bit neater
            editor.getCurrentTileMap().setFilename(filename);
            editor.updateRecent(filename);
            editor.getUndoHandler().commitSave();
            editor.updateTitle();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(editor.getAppFrame(),
                    SAVE_AS_ERROR_MESSAGE + " " +
                            filename + ": " +
                            e.getLocalizedMessage(),
                    SAVE_AS_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
