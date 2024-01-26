package org.github.logof.zxtiled.mapeditor.gui.menu;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.gui.TMenuItem;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import javax.swing.*;

public class EditMenu extends JMenu {

    public EditMenu() {
        super(Resources.getString("menu.edit"));
        initiation();
    }

    private void initiation() {
        JMenuItem copyMenuItem = new TMenuItem(MapEditorAction.copyAction);
        copyMenuItem.setEnabled(false);

        JMenuItem copyAllMenuItem = new TMenuItem(MapEditorAction.copyAllAction);
        copyAllMenuItem.setEnabled(false);

        JMenuItem cutMenuItem = new TMenuItem(MapEditorAction.cutAction);
        cutMenuItem.setEnabled(false);

        JMenuItem pasteMenuItem = new TMenuItem(MapEditorAction.pasteAction);
        pasteMenuItem.setEnabled(false);

        //this.add(new TMenuItem(undoHandler.getUndoAction()));
        //this.add(new TMenuItem(undoHandler.getRedoAction()));
        this.addSeparator();
        this.add(copyMenuItem);
        this.add(copyAllMenuItem);
        this.add(cutMenuItem);
        this.add(pasteMenuItem);
        this.addSeparator();

        JMenu transformSub = new JMenu(Resources.getString("menu.edit.transform"));
        transformSub.add(new TMenuItem(MapEditorAction.flipHorAction, true));
        transformSub.add(new TMenuItem(MapEditorAction.flipVerAction, true));
        MapEventAdapter.addListener(transformSub);
        this.add(transformSub);

        this.addSeparator();
        this.add(createMenuItem(Resources.getString("menu.edit.preferences"),
                null,
                Resources.getString("menu.edit.preferences.tooltip"),
                null));

        MapEventAdapter.addListener(copyMenuItem);
        MapEventAdapter.addListener(copyAllMenuItem);
        MapEventAdapter.addListener(cutMenuItem);
        MapEventAdapter.addListener(pasteMenuItem);
    }

    //TODO нужно передать листенеры
    private JMenuItem createMenuItem(String name,
                                     Icon icon,
                                     String tipText,
                                     String keyStroke) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(MapEditor.getActionListener());
        if (icon != null) {
            menuItem.setIcon(icon);
        }
        if (tipText != null) {
            menuItem.setToolTipText(tipText);
        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke));
        return menuItem;
    }
}
