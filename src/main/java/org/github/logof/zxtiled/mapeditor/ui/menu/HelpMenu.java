package org.github.logof.zxtiled.mapeditor.ui.menu;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;

public class HelpMenu extends JMenu {
    public HelpMenu() {
        super(Resources.getString("menu.help"));
        initiation();
    }

    private void initiation() {
        this.add(createMenuItem(Resources.getString("menu.help.plugins"), null,
                Resources.getString("menu.help.plugins.tooltip")));
        this.add(createMenuItem(Resources.getString("menu.help.about"), null, Resources.getString("menu.help.about.tooltip")));
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
