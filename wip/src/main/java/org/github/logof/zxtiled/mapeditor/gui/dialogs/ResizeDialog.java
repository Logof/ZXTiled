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

import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.IntegerSpinner;
import org.github.logof.zxtiled.mapeditor.gui.ResizePanel;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @version $Id$
 */
public class ResizeDialog extends JDialog implements ActionListener,
                                                     PropertyChangeListener,
                                                     ChangeListener {
    private static final String DIALOG_TITLE = Resources.getString("dialog.resizemap.title");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");
    private static final String OFFSET_TITLE = Resources.getString("dialog.resizemap.offset.title");
    private static final String X_LABEL = Resources.getString("dialog.resizemap.x.label");
    private static final String Y_LABEL = Resources.getString("dialog.resizemap.y.label");
    private static final String NEWSIZE_TITLE = Resources.getString("dialog.resizemap.newsize.title");
    private static final String WIDTH_LABEL = Resources.getString("dialog.resizemap.width.label");
    private static final String HEIGHT_LABEL = Resources.getString("dialog.resizemap.height.label");
    private static final String CURRENT_SIZE_TITLE = Resources.getString("dialog.resizemap.currentsize.title");
    private static final String RESIZE_LAYERS_TITLE = Resources.getString("dialog.resizemap.resizelayers.title");
    private final TileMap currentTileMap;
    private IntegerSpinner width, height, offsetX, offsetY;
    private JCheckBox resizeLayersCheckBox;
    private JButton bOk, bCancel;
    private ResizePanel orient;

    public ResizeDialog(JFrame parent, MapEditor m) {
        super(parent, DIALOG_TITLE, true);
        currentTileMap = m.getCurrentTileMap();
        init();
        setLocationRelativeTo(getOwner());
    }

    private void init() {
        // Create the primitives

        bOk = new JButton(OK_BUTTON);
        bCancel = new JButton(CANCEL_BUTTON);

        width = new IntegerSpinner(currentTileMap.getWidth(), 1);
        height = new IntegerSpinner(currentTileMap.getHeight(), 1);
        offsetX = new IntegerSpinner();
        offsetY = new IntegerSpinner();
        resizeLayersCheckBox = new JCheckBox(RESIZE_LAYERS_TITLE, true);

        offsetX.addChangeListener(this);
        offsetY.addChangeListener(this);
        resizeLayersCheckBox.addActionListener(this);

        orient = new ResizePanel(currentTileMap);
        orient.addPropertyChangeListener(this);

        // Offset panel
        JPanel offsetPanel = new VerticalStaticJPanel();
        offsetPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(OFFSET_TITLE),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        offsetPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(5, 0, 0, 5);
        offsetPanel.add(new JLabel(X_LABEL), c);
        c.gridy = 1;
        offsetPanel.add(new JLabel(Y_LABEL), c);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 0, 0, 0);
        offsetPanel.add(offsetX, c);
        c.gridy = 1;
        offsetPanel.add(offsetY, c);
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.weightx = 1;
        offsetPanel.add(new JPanel(), c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.gridheight = 1;
        //offsetPanel.add(orient, c);

        // New size panel
        JPanel newSizePanel = new VerticalStaticJPanel(new GridBagLayout());
        newSizePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(NEWSIZE_TITLE),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(5, 0, 0, 5);
        newSizePanel.add(new JLabel(WIDTH_LABEL), c);
        c.gridy = 1;
        newSizePanel.add(new JLabel(HEIGHT_LABEL), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.insets = new Insets(5, 0, 0, 0);
        newSizePanel.add(width, c);
        c.gridy = 1;
        newSizePanel.add(height, c);

        // Original size panel
        JPanel origSizePanel = new VerticalStaticJPanel(new GridBagLayout());
        origSizePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(CURRENT_SIZE_TITLE),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        c.insets = new Insets(5, 0, 0, 5);
        origSizePanel.add(new JLabel(WIDTH_LABEL), c);
        c.gridy = 1;
        origSizePanel.add(new JLabel(HEIGHT_LABEL), c);
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 10, 0, 0);
        c.gridx = 1;
        c.gridy = 0;
        origSizePanel.add(new JLabel(String.valueOf(currentTileMap.getWidth())), c);
        c.gridy = 1;
        origSizePanel.add(new JLabel(String.valueOf(currentTileMap.getHeight())), c);

        // Putting two size panels next to eachother
        JPanel sizePanels = new VerticalStaticJPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1;
        c.weightx = 0;
        sizePanels.add(origSizePanel, c);
        c.gridx = 1;
        c.weightx = 1;
        sizePanels.add(newSizePanel, c);

        // Buttons panel
        bOk.addActionListener(this);
        bCancel.addActionListener(this);
        JPanel buttons = new VerticalStaticJPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(bOk);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(bCancel);

        // Putting the main panel together
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(sizePanels);
        mainPanel.add(resizeLayersCheckBox);
        mainPanel.add(offsetPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(Box.createGlue());
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(bOk);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == bOk) {


            int nwidth = width.intValue();
            int nheight = height.intValue();
            int dx = offsetX.intValue();
            int dy = offsetY.intValue();
            boolean resizeLayers = resizeLayersCheckBox.isSelected();

            // Math works out in MapLayer#resize
            if (resizeLayers) {
                currentTileMap.resize(nwidth, nheight, dx, dy);
            } else {
                currentTileMap.resize(nwidth, nheight);
            }

            dispose();
        } else if (src == bCancel) {
            dispose();
        } else if (src == resizeLayersCheckBox) {
            boolean allowResize = resizeLayersCheckBox.isSelected();
            offsetX.setEnabled(allowResize);
            offsetY.setEnabled(allowResize);
        } else {
            System.out.println(e.getActionCommand());
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        double zoom = orient.getZoom();

        if (evt.getPropertyName().equalsIgnoreCase("offsetX")) {
            int val = (Integer) evt.getNewValue();
            offsetX.setValue((int) (val / (Constants.TILE_WIDTH * zoom)));
        } else if (evt.getPropertyName().equalsIgnoreCase("offsetY")) {
            int val = (Integer) evt.getNewValue();
            offsetY.setValue((int) (val / (Constants.TILE_HEIGHT * zoom)));
        }
    }

    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source == offsetX || source == offsetY) {
            int dx = offsetX.intValue();
            int dy = offsetY.intValue();
            double zoom = orient.getZoom();

            orient.moveMap(
                    (int) (dx * Constants.TILE_WIDTH * zoom),
                    (int) (dy * Constants.TILE_HEIGHT * zoom));
        }
    }
}
