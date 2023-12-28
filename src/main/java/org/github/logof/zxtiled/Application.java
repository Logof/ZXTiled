/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.util.TiledConfiguration;
import java.io.File;
import java.util.List;

/**
 *
 * @author upachler
 */
public class Application {
    /**
     * Starts Tiled.
     *
     * @param args the first argument may be a map file
     */
    public static void main(String[] args) {
        MapEditor editor = new MapEditor();
        if (TiledConfiguration.node("io").getBoolean("autoOpenLast", false)) {
            // Load last map if it still exists
            List<String> recent = TiledConfiguration.getRecentFiles();
            if (!recent.isEmpty()) {
                String filename = recent.get(0);
                if (new File(filename).exists()) {
                    editor.loadMap(filename);
                }
            }
        }
    }
}

