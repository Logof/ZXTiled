package org.github.logof.zxtiled.mapeditor.gui.panel;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import org.github.logof.zxtiled.mapeditor.gui.StatusBar;
import org.github.logof.zxtiled.mapeditor.gui.TabbedTilesetsPane;
import org.github.logof.zxtiled.mapeditor.gui.ToolBar;
import javax.swing.*;
import java.awt.*;

public class MapEditorMainPanel extends AbstractPanel {

    private MapEditor mapEditor;

    public MapEditorMainPanel(JFrame parentFrame, MapEditor mapEditor) {
        super(parentFrame, true);
    }

    @Override
    protected void initComponent() {
        setLayout(new BorderLayout());

        ToolBar toolBar = new ToolBar();
        StatusBar statusBar = new StatusBar();


        add(toolBar, BorderLayout.WEST);
        add(createMapPanel(), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createMapPanel() {
        JPanel mapPanel = new JPanel();
        mapPanel.setLayout(new BorderLayout());

        mapPanel.add(new GraphicsPanel(), BorderLayout.NORTH);
        mapPanel.add(new TabbedTilesetsPane(mapEditor), BorderLayout.CENTER);
        return mapPanel;
    }

    private JPanel createTilesetPanel() {
        JPanel tilesetPanel = new JPanel();
        tilesetPanel.setLayout(new BorderLayout());
        tilesetPanel.add(new JLabel("TILESET LABEL"), BorderLayout.WEST);
        return tilesetPanel;
    }
}
