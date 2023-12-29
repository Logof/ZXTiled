/*
 *  Tiled Map Editor, (c) 2008
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
import org.github.logof.zxtiled.mapeditor.util.ConfirmableFileFilter;
import org.github.logof.zxtiled.mapeditor.util.ConfirmingFileChooser;
import org.github.logof.zxtiled.view.MapView;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Saves the map to an image.
 *
 * @version $Id$
 * @noinspection serial
 */
public class SaveAsImageAction extends AbstractAction {
    private static final String ACTION_NAME = Resources.getString("action.map.saveasimage.name");
    private static final String ACTION_TOOLTIP = Resources.getString("action.map.saveasimage.tooltip");
    private static final String DIALOG_TITLE = Resources.getString("dialog.saveasimage.title");
    private final MapEditor editor;
    private final Frame appFrame;

    public SaveAsImageAction(MapEditor editor) {
        super(ACTION_NAME);

        putValue(SHORT_DESCRIPTION, ACTION_TOOLTIP);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift I"));

        this.editor = editor;
        appFrame = editor.getAppFrame();
    }

    public void actionPerformed(ActionEvent event) {
        if (editor.getCurrentMap() == null)
            return;

        JFileChooser chooser = new ConfirmingFileChooser();
        chooser.setDialogTitle(DIALOG_TITLE);
        final FileFilter defaultFilter = chooser.getFileFilter();

        final String[] names = ImageIO.getWriterFormatNames();
        for (String name : names) {
            if (name.matches("[A-Z].*"))
                chooser.addChoosableFileFilter(
                        new BasicFileFilter(name, name.toLowerCase()));
        }

        // Make sure the "All Files" filter is selected by default
        chooser.setFileFilter(defaultFilter);

        if (chooser.showSaveDialog(appFrame) ==
                JFileChooser.APPROVE_OPTION) {
            saveMapImage(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Attempts to draw the entire map to an image file of the format of the
     * extension. (filename.ext)
     *
     * @param filename Image filename to save map render to.
     */
    private void saveMapImage(String filename) {
        final Map currentMap = editor.getCurrentMap();
        final MapView myView = MapView.createViewforMap(currentMap);
        myView.setMode(MapView.PF_NO_SPECIAL, true);

        // Take grid and zoom level from the current map view
        final MapView mapView = editor.getMapView();
        myView.setShowGrid(mapView.getShowGrid());
        myView.setZoom(mapView.getZoom());

        final Dimension imgSize = myView.getPreferredSize();

        final int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            JOptionPane.showMessageDialog(appFrame,
                    "No file format specified.",
                    "Error while saving map image",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        final String format = filename.substring(lastDot + 1);

        try {
            BufferedImage img = new BufferedImage(
                    imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setClip(0, 0, imgSize.width, imgSize.height);
            myView.paint(g);

            try {
                ImageIO.write(img, format, new File(filename));
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(appFrame,
                        "Error while saving " + filename + ": " + e,
                        "Error while saving map image",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (OutOfMemoryError memoryError) {
            JOptionPane.showMessageDialog(appFrame,
                    "Out of memory while creating image. Try increasing\n" +
                            "your maximum heap size or zooming out a bit.",
                    "Out of memory",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public class BasicFileFilter extends ConfirmableFileFilter {
        private final String extension;
        private final String description;

        public BasicFileFilter(String description, String extension) {
            this.description = description;
            this.extension = extension;
        }

        public String getDefaultExtension() {
            return extension;
        }

        public boolean accept(File file) {
            String fileName = file.getPath().toLowerCase();
            return file.isDirectory() || fileName.endsWith("." + extension);
        }

        public String getDescription() {
            return description + " (*." + extension + ")";
        }
    }
}
