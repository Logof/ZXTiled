package org.github.logof.zxtiled.mapeditor.gui.panel;

import lombok.Getter;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import org.github.logof.zxtiled.mapeditor.gui.IntegerSpinner;
import javax.swing.*;
import java.awt.*;

@Getter
public class MapScreenSizePanel extends AbstractPanel {
    private static final String MAP_SIZE_TITLE = Resources.getString("dialog.newmap.mapsize.title");
    private static final String WIDTH_LABEL = Resources.getString("dialog.newmap.width.label");
    private static final String HEIGHT_LABEL = Resources.getString("dialog.newmap.height.label");

    private IntegerSpinner mapWidthSpinner;
    private IntegerSpinner mapHeightSpinner;

    public MapScreenSizePanel(JDialog parentDialog) {
        super(parentDialog);
    }

    @Override
    protected void initComponent() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(MAP_SIZE_TITLE),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        mapWidthSpinner = new IntegerSpinner(1, 1, 64);
        mapHeightSpinner = new IntegerSpinner(1, 1, 64);

        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        add(new JLabel(WIDTH_LABEL), gridBagConstraints);
        gridBagConstraints.gridy = 1;
        add(new JLabel(HEIGHT_LABEL), gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        add(mapWidthSpinner, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        add(mapHeightSpinner, gridBagConstraints);

    }
}
