package org.github.logof.zxtiled.mapeditor.gui.menu;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import javax.swing.*;

public class TilesetMenu extends JMenu {
    public TilesetMenu() {
        super(Resources.getString("menu.tilesets"));
        initiation();
    }

    private void initiation() {
        this.add(createMenuItem(
                Resources.getString("menu.tilesets.new"), null,
                Resources.getString("menu.tilesets.new.tooltip")));
        this.add(createMenuItem(
                Resources.getString("menu.tilesets.import"), null,
                Resources.getString("menu.tilesets.import.tooltip")));
        this.addSeparator();
        this.add(createMenuItem(
                Resources.getString("menu.tilesets.refresh"), null,
                Resources.getString("menu.tilesets.refresh.tooltip"), "F5"));
        this.addSeparator();
        this.add(createMenuItem(
                Resources.getString("menu.tilesets.manager"), null,
                Resources.getString("menu.tilesets.manager.tooltip")));

        MapEventAdapter.addListener(this);
    }

    private JMenuItem createMenuItem(String name, Icon icon, String tipText,
                                     String keyStroke) {
        JMenuItem menuItem = createMenuItem(name, icon, tipText);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke));
        return menuItem;
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
