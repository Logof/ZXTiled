package org.github.logof.zxtiled.mapeditor.ui.menu;

import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;

public class FileMenu extends JMenu {


    public FileMenu() {
        super(Resources.getString("menu.file"));
        initiation();
    }

    private void initiation() {
        /*JMenuItem save = new TMenuItem(saveAction);
        JMenuItem saveAs = new TMenuItem(saveAsAction);
        JMenuItem saveAsImage = new TMenuItem(saveAsImageAction);
        JMenuItem exportMap = new TMenuItem(exportAction);
        JMenuItem close = new TMenuItem(new CloseMapAction(this, saveAction));

        MapEventAdapter.addListener(save);
        MapEventAdapter.addListener(saveAs);
        MapEventAdapter.addListener(saveAsImage);
        MapEventAdapter.addListener(exportMap);
        MapEventAdapter.addListener(close);

        RecentMenu recentMenu = new RecentMenu();

        this.add(new TMenuItem(new NewMapAction(this, saveAction)));
        this.add(new TMenuItem(new OpenMapAction(this, saveAction)));
        this.add(recentMenu);
        this.add(save);
        this.add(saveAs);
        this.add(saveAsImage);
        this.add(exportMap);
        this.addSeparator();
        this.add(close);
        this.add(new TMenuItem(exitAction));
        */
    }

}
