package org.github.logof.zxtiled.mapeditor.gui;

import lombok.Getter;
import javax.swing.*;
import java.awt.*;

@Getter
public class StatusBar extends JPanel {
    private JLabel zoomLabel;
    private JLabel tilePositionLabel;
    private JPanel largePart;
    private TimedStatusLabel statusLabel;

    public StatusBar() {
        super();
        initiation();
    }

    private void initiation() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        zoomLabel = new JLabel("100%");
        zoomLabel.setPreferredSize(zoomLabel.getPreferredSize());

        tilePositionLabel = new JLabel(" ", SwingConstants.CENTER);

        this.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        largePart = new JPanel();

        statusLabel = new TimedStatusLabel(5);

        this.add(statusLabel);
        this.add(largePart);
        this.add(tilePositionLabel);
        this.add(Box.createRigidArea(new Dimension(20, 0)));
        this.add(zoomLabel);
    }
}
