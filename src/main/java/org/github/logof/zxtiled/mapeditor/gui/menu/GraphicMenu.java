package org.github.logof.zxtiled.mapeditor.gui.menu;

import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;

public class GraphicMenu extends JMenu {
    public GraphicMenu() {
        super(Resources.getString("menu.graphics"));
        initiation();
    }

    private void initiation() {
        add(new TilesetMenu());
        add(new SpriteMenu());
    }
}
