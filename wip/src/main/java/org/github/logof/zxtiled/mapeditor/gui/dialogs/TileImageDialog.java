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
 *  Rainer Deyke <rainerd@eldwood.com>
 */

package org.github.logof.zxtiled.mapeditor.gui.dialogs;

import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import org.github.logof.zxtiled.mapeditor.util.ImageCellRenderer;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Dialog for picking a tile image. Used by the edit tileset dialog for
 * changing the image of a tile.
 */
public class TileImageDialog extends JDialog implements ListSelectionListener {
    private static final String DIALOG_TITLE = Resources.getString("dialog.tileimage.title");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");
    private final Tileset tileset;
    private JList imageList;
    private JButton okButton;
    private JButton cancelButton;
    private int imageId;
    private JLabel imageLabel;
    private int[] imageIds;

    public TileImageDialog(Dialog parent, Tileset set) {
        this(parent, set, 0);
    }

    public TileImageDialog(Dialog parent, Tileset set, int id) {
        super(parent, DIALOG_TITLE, true);
        tileset = set;
        imageId = id;

        init();
        queryImages();
        updateImageLabel();
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void init() {
        // image list
        imageList = new JList();
        imageList.setCellRenderer(new ImageCellRenderer());
        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imageList.addListSelectionListener(this);
        JScrollPane sp = new JScrollPane();
        sp.getViewport().setView(imageList);
        sp.setPreferredSize(new Dimension(150, 150));

        // image panel
        JPanel image_panel = new JPanel();
        image_panel.setLayout(new BoxLayout(image_panel, BoxLayout.Y_AXIS));
        imageLabel = new JLabel(new ImageIcon());

        image_panel.add(imageLabel);

        // buttons
        okButton = new JButton(OK_BUTTON);
        cancelButton = new JButton(CANCEL_BUTTON);
        JPanel buttons = new VerticalStaticJPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(cancelButton);
        buttons.add(okButton);

        // main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        mainPanel.add(sp, c);
        c.weightx = 0;
        c.gridx = 1;
        mainPanel.add(image_panel, c);
        c.gridx = 0;
        c.weighty = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        mainPanel.add(buttons, c);
        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(okButton);

        //create action listeners
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                imageId = -1;
                dispose();
            }
        });
    }

    public void queryImages() {
        Vector<Image> listData = new Vector<Image>();
        int initialIndex = 0;

        Enumeration<String> ids = tileset.getImageIds();
        imageIds = new int[tileset.getTotalImages()];
        for (int i = 0; i < imageIds.length; ++i) {
            imageIds[i] = Integer.parseInt(ids.nextElement());
        }

        Arrays.sort(imageIds);

        for (int i = 0; i < imageIds.length; ++i) {
            if (imageIds[i] == imageId) initialIndex = i;
            Image img = tileset.getImageById(imageIds[i]);
            // assert img != null;
            listData.add(img);
        }

        imageList.setListData(listData);
        imageList.setSelectedIndex(initialIndex);
        imageList.ensureIndexIsVisible(initialIndex);
    }

    private void updateEnabledState() {
        okButton.setEnabled(imageId >= 0);
    }

    private void updateImageLabel() {
        if (imageId >= 0) {
            Image img = tileset.getImageById(imageId);
            imageLabel.setIcon(new ImageIcon(img));
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        imageId = imageIds[imageList.getSelectedIndex()];
        updateImageLabel();
        updateEnabledState();
    }

    public int getImageId() {
        return imageId;
    }
}
