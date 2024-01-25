package org.github.logof.prototype.ui.panel;

import org.github.logof.prototype.ui.AbstractPanel;
import javax.swing.*;
import java.awt.*;

public class ProjectPanel extends AbstractPanel {

    public ProjectPanel(JPanel parentPanel) {
        super(parentPanel);
    }

    @Override
    protected void initComponent() {
        setLayout(new GridBagLayout());
        JLabel projectNameLabel = new JLabel();
        JTextField projectName = new JTextField();
        projectNameLabel.setLabelFor(projectName);

        GridBagConstraints grid = new GridBagConstraints();
        grid.gridx = 0;
        grid.gridy = 0;
        add(projectNameLabel, grid);
        grid.gridy = 1;
        add(projectName, grid);
    }
}
