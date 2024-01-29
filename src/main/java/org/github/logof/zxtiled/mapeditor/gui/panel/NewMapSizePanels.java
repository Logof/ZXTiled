package org.github.logof.zxtiled.mapeditor.gui.panel;

import lombok.Getter;
import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import javax.swing.*;
import java.awt.*;

@Getter
public class NewMapSizePanels extends AbstractPanel {

    private TileBmpPathPanel tileBmpPathPanel;
    private MapScreenSizePanel mapScreenSizePanel;

    public NewMapSizePanels(JPanel parentPanel) {
        super(parentPanel);
    }

    @Override
    protected void initComponent() {
        tileBmpPathPanel = new TileBmpPathPanel(this);
        mapScreenSizePanel = new MapScreenSizePanel(this);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(mapScreenSizePanel);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(tileBmpPathPanel);
    }
}
