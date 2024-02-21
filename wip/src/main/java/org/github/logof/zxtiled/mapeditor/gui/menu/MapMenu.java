package org.github.logof.zxtiled.mapeditor.gui.menu;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import javax.swing.*;

public class MapMenu extends JMenu {
    public MapMenu() {
        super(Resources.getString("menu.map"));
        initiation();
    }

    private void initiation() {
        this.add(createMenuItem(Resources.getString("menu.map.resize"), null,
                Resources.getString("menu.map.resize.tooltip")));
        this.addSeparator();
        this.add(createMenuItem(Resources.getString("menu.map.properties"), null,
                Resources.getString("menu.map.properties.tooltip")));
        MapEventAdapter.addListener(this);
    }

    private JMenuItem createMenuItem(String name, Icon icon, String tipText) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(MapEditor.getActionListener());
        if (icon != null) {
            menuItem.setIcon(icon);
        }
        if (tipText != null) {
            menuItem.setToolTipText(tipText);
        }
        return menuItem;
    }
}
