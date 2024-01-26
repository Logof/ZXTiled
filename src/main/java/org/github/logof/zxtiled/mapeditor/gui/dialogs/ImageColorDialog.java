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

import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.ImageViewPanel;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.PixelGrabber;

public class ImageColorDialog extends JDialog implements MouseListener,
                                                         MouseMotionListener {
    /* LANGUAGE PACK */
    private static final String DIALOG_TITLE = Resources.getString("dialog.imagecolor.title");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");
    private final Image image;
    private JButton cancelButton;
    private Color color;
    private JPanel colorPanel;
    private final int[] pixels;
    /* -- */

    public ImageColorDialog(Image image) {
        this.image = image;
        PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, true);

        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pixels = (int[]) pg.getPixels();

        init();
        pack();
        setLocationRelativeTo(getOwner());
        setModal(true);
    }

    private void init() {
        ImageViewPanel imagePanel = new ImageViewPanel(image);
        imagePanel.addMouseListener(this);
        imagePanel.addMouseMotionListener(this);

        setTitle(DIALOG_TITLE);

        color = new Color(255, 103, 139);  //Evil pink
        colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(25, 25));
        colorPanel.setBackground(color);

        cancelButton = new JButton(CANCEL_BUTTON);

        JScrollPane imageScrollPane = new JScrollPane(imagePanel);
        imageScrollPane.setAutoscrolls(true);

        VerticalStaticJPanel mainPanel = new VerticalStaticJPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(imageScrollPane);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(colorPanel);
        c.gridx = 1;
        buttonPanel.add(Box.createRigidArea(new Dimension(25, 5)));
        c.gridx = 2;
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);

        //create actionlisteners
        cancelButton.addActionListener(actionEvent -> {
            color = null;
            dispose();
        });
    }

    public Color showDialog() {
        setVisible(true);
        return color;
    }

    public void mouseClicked(MouseEvent e) {
        grabColor(e.getX(), e.getY());
        dispose();
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        grabColor(e.getX(), e.getY());
    }

    public void mouseMoved(MouseEvent e) {
        grabColor(e.getX(), e.getY());
    }

    private void grabColor(int x, int y) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        if (pixels != null && (x < w && y < h)) {
            int r = pixels[y * w + x] >> 16 & 0xff;
            int g = pixels[y * w + x] >> 8 & 0xff;
            int b = pixels[y * w + x] & 0xff;

            color = new Color(r, g, b);
            colorPanel.setBackground(color);
        }
    }
}
