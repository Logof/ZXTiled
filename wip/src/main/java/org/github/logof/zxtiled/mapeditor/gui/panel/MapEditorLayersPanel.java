package org.github.logof.zxtiled.mapeditor.gui.panel;

import lombok.Getter;
import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import javax.swing.*;

@Getter
public class MapEditorLayersPanel extends AbstractPanel {
    private final JPanel contentPane = new JPanel();

    public MapEditorLayersPanel(JFrame parentFrame) {
        super(parentFrame);
    }

    @Override
    protected void initComponent() {

    }

    public void save() {
    }

    public void restore() {
    }
}
