package org.github.logof.zxtiled.mapeditor.ui.menu;

import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.ui.TMenuItem;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import javax.swing.*;

public class SelectMenu extends JMenu {
    public SelectMenu() {
        super(Resources.getString("menu.select"));
        initiation();
    }

    private void initiation() {
        this.add(new TMenuItem(MapEditorAction.selectAllAction, true));
        this.add(new TMenuItem(MapEditorAction.cancelSelectionAction, true));
        this.add(new TMenuItem(MapEditorAction.inverseAction, true));

        MapEventAdapter.addListener(this);
    }
}
