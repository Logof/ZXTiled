package org.github.logof.zxtiled.mapeditor.gui.panel;

import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import javax.swing.*;
import java.awt.*;

public class MiscPropertiesPanel extends AbstractPanel {

    private static final String MAP_TYPE_SIDE_SCROLLED = Resources.getString("general.map.type.sidescrolled");
    private static final String MAP_TYPE_TOP_DOWN = Resources.getString("general.map.type.topdown");
    private static final String MAP_TYPE_LABEL = Resources.getString("dialog.newmap.maptype.label");

    public MiscPropertiesPanel(JDialog parentDialog) {
        super(parentDialog);
    }

    @Override
    protected void initComponent() {
        JComboBox<String> mapTypeChooser = new JComboBox<>();
        mapTypeChooser.addItem(MAP_TYPE_SIDE_SCROLLED);
        mapTypeChooser.addItem(MAP_TYPE_TOP_DOWN);

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        add(new JLabel(MAP_TYPE_LABEL), gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        add(mapTypeChooser, gridBagConstraints);

    }
}
