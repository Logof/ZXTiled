package org.github.logof.prototype.ui.panel;

import org.github.logof.prototype.ui.AbstractPanel;
import javax.swing.*;
import java.awt.*;

public class SettingPanel extends AbstractPanel {

    public SettingPanel(JPanel parentPanel) {
        super(parentPanel);
    }

    @Override
    protected void initComponent() {
        setLayout(new GridBagLayout());
        JLabel mapSizeLabel = new JLabel();
        JTextField mapSize = new JTextField();
        mapSizeLabel.setLabelFor(mapSize);

        GridBagConstraints grid = new GridBagConstraints();
        grid.gridx = 0;
        grid.gridy = 0;
        add(mapSizeLabel, grid);
        grid.gridy = 1;
        add(mapSize, grid);
    }
}
