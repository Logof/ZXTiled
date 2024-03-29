package org.github.logof.zxtiled.mapeditor.gui;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.listener.ApplicationFrameWindowAdapter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.prefs.Preferences;

public class ApplicationFrame extends JFrame {

    private static final int APP_WIDTH = 800;
    private static final int APP_HEIGHT = 600;

    private int state = 0;

    WindowAdapter windowAdapter = new ApplicationFrameWindowAdapter();

    public ApplicationFrame() {
        super(Resources.getString("dialog.main.title"));
        init();
    }

    private void init() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        final Preferences mainDialogPreferences = MapEditor.PREFERENCES.node("dialog/main");
        final int width = mainDialogPreferences.getInt("width", APP_WIDTH);
        final int height = mainDialogPreferences.getInt("height", APP_HEIGHT);
        this.setSize(width, height);
        this.state = mainDialogPreferences.getInt("state", Frame.NORMAL);
    }

    public void updateExtendedState() {
        if (state != Frame.ICONIFIED) {
            this.setExtendedState(state);
        }
    }
}
