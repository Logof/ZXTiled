package org.github.logof.zxtiled.mapeditor.gui.dialogs_new;

import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.core.objects.HotspotObject;
import org.github.logof.zxtiled.core.objects.MapObject;
import org.github.logof.zxtiled.core.objects.MovingObject;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.*;
import java.util.Vector;

public class OpenDialogs {
    private static final String DIALOG_TITLE = Resources.getString("dialog.object.title");

    public static void openObjectDialog(JFrame parent, MapObject mapObject, Vector<Tileset> tilesets) {
        JDialog dialog;
        if (mapObject instanceof MovingObject) {
            dialog = new MovingObjectDialog((MovingObject) mapObject, tilesets);
        } else {
            dialog = new HotspotDialog((HotspotObject) mapObject, tilesets);
        }
        dialog.setSize(400, 245);
        dialog.setTitle(DIALOG_TITLE);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}