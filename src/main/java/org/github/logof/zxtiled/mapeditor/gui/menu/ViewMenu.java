package org.github.logof.zxtiled.mapeditor.gui.menu;

import lombok.Getter;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.gui.TMenuItem;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import javax.swing.*;

@Getter
public class ViewMenu extends JMenu {
    private JCheckBoxMenuItem gridMenuItem;
    private JCheckBoxMenuItem cursorMenuItem;
    private JCheckBoxMenuItem coordinatesMenuItem;


    public ViewMenu() {
        super(Resources.getString("menu.view"));
        initiation();
    }

    private void initiation() {
        gridMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.grid"));
        gridMenuItem.addActionListener(MapEditor.getActionListener());
        gridMenuItem.setToolTipText(Resources.getString("menu.view.grid.tooltip"));
        gridMenuItem.setAccelerator(KeyStroke.getKeyStroke("control G"));

        cursorMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.cursor"));
        cursorMenuItem.setSelected(MapEditor.preferences.getBoolean("cursorhighlight", true));
        cursorMenuItem.addActionListener(MapEditor.getActionListener());
        cursorMenuItem.setToolTipText(Resources.getString("menu.view.cursor.tooltip"));

        coordinatesMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.coordinates"));
        coordinatesMenuItem.addActionListener(MapEditor.getActionListener());
        coordinatesMenuItem.setToolTipText(Resources.getString("menu.view.coordinates.tooltip"));

        this.add(new TMenuItem(MapEditorAction.zoomInAction));
        this.add(new TMenuItem(MapEditorAction.zoomOutAction));
        this.add(new TMenuItem(MapEditorAction.zoomNormalAction));
        this.addSeparator();
        this.add(gridMenuItem);
        this.add(cursorMenuItem);
        this.add(coordinatesMenuItem);

        MapEventAdapter.addListener(this);
    }
}
