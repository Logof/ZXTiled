package org.github.logof.zxtiled.mapeditor.ui.menu;

import lombok.Getter;
import javax.swing.*;

@Getter
public class MainMenuBar extends JMenuBar {


    private final FileMenu fileMenu;
    private final EditMenu editMenu;
    private final ViewMenu viewMenu;
    public MainMenuBar() {
        super();
        fileMenu = new FileMenu();
        editMenu = new EditMenu();
        SelectMenu selectMenu = new SelectMenu();
        viewMenu = new ViewMenu();
        MapMenu mapMenu = new MapMenu();
        LayerMenu layerMenu = new LayerMenu();
        TilesetMenu tilesetMenu = new TilesetMenu();
        HelpMenu helpMenu = new HelpMenu();

        this.add(fileMenu);
        this.add(editMenu);
        this.add(selectMenu);
        this.add(viewMenu);
        this.add(mapMenu);
        this.add(layerMenu);
        this.add(tilesetMenu);
        this.add(helpMenu);
    }
}
