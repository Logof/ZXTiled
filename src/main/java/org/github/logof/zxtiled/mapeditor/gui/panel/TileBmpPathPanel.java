package org.github.logof.zxtiled.mapeditor.gui.panel;

import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import javax.swing.*;
import java.awt.*;

public class TileBmpPathPanel extends AbstractPanel {

    private static final String TILESET_IMAGE_LABEL = Resources.getString("dialog.newtileset.image.label");
    private static final String TILESET_NAME_LABEL = Resources.getString("dialog.newtileset.name.label");

    public TileBmpPathPanel(JDialog parentDialog) {
        super(parentDialog);
    }

    @Override
    protected void initComponent() {
        JLabel tilesetNameLabel = new JLabel(TILESET_NAME_LABEL);
        JTextField tilesetName = new JTextField("UNTITLED_FILE");
        tilesetNameLabel.setLabelFor(tilesetName);

        JLabel tileBmpFileLabel = new JLabel(TILESET_IMAGE_LABEL);
        JTextField tileBmpFileField = new JTextField(10);
        tileBmpFileLabel.setLabelFor(tileBmpFileField);

        JButton browseButton = new JButton("BROWSE_BUTTON");

        setLayout(new GridBagLayout());
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.gridx = 0;
        bagConstraints.gridy = 0;
        bagConstraints.weightx = 1;
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(tileBmpFileField, bagConstraints);
        bagConstraints.gridx = 1;
        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.NONE;
        bagConstraints.insets = new Insets(0, 5, 0, 0);
        add(browseButton, bagConstraints);
    }
}
