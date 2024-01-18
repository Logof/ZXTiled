package org.github.logof.zxtiled.mapeditor.ui.menu;

import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.ui.TMenuItem;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import javax.swing.*;

public class LayerMenu extends JMenu {
    public LayerMenu() {
        super(Resources.getString("menu.layer"));
        initiation();
    }

    private void initiation() {
        JMenuItem layerAdd = new TMenuItem(MapEditorAction.addLayerAction);
        MapEventAdapter.addListener(layerAdd);
        this.add(layerAdd);
        this.add(new TMenuItem(MapEditorAction.cloneLayerAction));
        this.add(new TMenuItem(MapEditorAction.deleteLayerAction));
        this.addSeparator();
        this.add(new TMenuItem(MapEditorAction.addObjectGroupAction));
        this.addSeparator();
        this.add(new TMenuItem(MapEditorAction.moveLayerUpAction));
        this.add(new TMenuItem(MapEditorAction.moveLayerDownAction));
        this.addSeparator();
        this.add(new TMenuItem(MapEditorAction.mergeLayerDownAction));
        this.add(new TMenuItem(MapEditorAction.mergeAllLayersAction));
        this.addSeparator();
        this.add(MapEditorAction.showLayerPropertiesAction);

        MapEventAdapter.addListener(this);

    }
}
