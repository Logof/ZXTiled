package org.github.logof.zxtiled.prototype;

import org.github.logof.zxtiled.prototype.gui.MainWindow;

/**
 * Класс, который отвечает за взаимодействие между GUI и объектами
 */
public class MapEditor {
    private final MainWindow mainWindow;


    public MapEditor() {
        this.mainWindow = new MainWindow();


        mainWindow.setVisible(true);
    }


}
