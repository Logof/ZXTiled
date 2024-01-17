package org.github.logof.zxtiled.mapeditor.ui.menu;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.ui.TMenuItem;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import javax.swing.*;

@Getter
@Setter
public class FileMenu extends JMenu {

    private RecentMenu recentMenu = new RecentMenu();

    public FileMenu() {
        super(Resources.getString("menu.file"));
        initiation();
    }

    private void initiation() {
        JMenuItem save = new TMenuItem(MapEditorAction.saveAction);
        JMenuItem saveAs = new TMenuItem(MapEditorAction.saveAsAction);
        JMenuItem saveAsImage = new TMenuItem(MapEditorAction.saveAsImageAction);
        JMenuItem exportMap = new TMenuItem(MapEditorAction.exportAction);
        JMenuItem close = new TMenuItem(MapEditorAction.closeMapAction);

        MapEventAdapter.addListener(save);
        MapEventAdapter.addListener(saveAs);
        MapEventAdapter.addListener(saveAsImage);
        MapEventAdapter.addListener(exportMap);
        MapEventAdapter.addListener(close);

        this.add(new TMenuItem(MapEditorAction.newMapAction));
        this.add(new TMenuItem(MapEditorAction.openMapAction));
        this.add(recentMenu);
        this.add(save);
        this.add(saveAs);
        this.add(saveAsImage);
        this.add(exportMap);
        this.addSeparator();
        this.add(close);
        this.add(new TMenuItem(MapEditorAction.exitAction));
    }

}
