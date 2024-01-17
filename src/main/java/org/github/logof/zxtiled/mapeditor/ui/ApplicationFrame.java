package org.github.logof.zxtiled.mapeditor.ui;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.listener.ApplicationFrameWindowAdapter;
import org.github.logof.zxtiled.mapeditor.ui.menu.MainMenuBar;
import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class ApplicationFrame extends JFrame {

    private static final int APP_WIDTH = 800;
    private static final int APP_HEIGHT = 600;

    private int state = 0;

    ApplicationFrameWindowAdapter windowAdapter = new ApplicationFrameWindowAdapter();

    public ApplicationFrame() {
        super(Resources.getString("dialog.main.title"));
        init();
        this.setJMenuBar(new MainMenuBar());
    }

    private void init() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        final Preferences mainDialogPrefs = MapEditor.preferences.node("dialog/main");
        final int width = mainDialogPrefs.getInt("width", APP_WIDTH);
        final int height = mainDialogPrefs.getInt("height", APP_HEIGHT);
        this.setSize(width, height);
        this.state = mainDialogPrefs.getInt("state", Frame.NORMAL);
    }

    public void updateExtendedState() {
        if (state != Frame.ICONIFIED) {
            this.setExtendedState(state);
        }
    }
}
