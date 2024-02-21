package org.github.logof.zxtiled.mapeditor.gui.menu;

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
        GraphicMenu graphicMenu = new GraphicMenu();
        HelpMenu helpMenu = new HelpMenu();

        add(fileMenu);
        add(editMenu);
        add(selectMenu);
        add(viewMenu);
        add(mapMenu);
        add(graphicMenu);
        add(helpMenu);
    }
}
