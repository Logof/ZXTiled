/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package org.github.logof.zxtiled.mapeditor.dialogs;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import org.github.logof.zxtiled.mapeditor.ui.VerticalStaticJPanel;
import org.github.logof.zxtiled.mapeditor.util.PropertiesTableModel;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;

/**
 * @version $Id$
 */
public class TileInstancePropertiesDialog extends JDialog
        implements TableModelListener {
    private static final String DIALOG_TITLE = "Tile Properties"; // todo: Resource this
    private static final String APPLY_BUTTON = Resources.getString("general.button.apply");
    private static final String APPLY_TOOLTIP = "Apply properties to selected tiles"; // todo: Resource this
    private static final String DELETE_BUTTON = Resources.getString("general.button.delete");
    private final MapEditor editor;
    /**
     * Holds all currently selected Properties.
     */
    private final LinkedList<Point> propertiesCoordinates = new LinkedList<>();
    private final Properties mergedProperties = new Properties();
    private JTable propertiesTable;
    private final PropertiesTableModel tableModel = new PropertiesTableModel();

    public TileInstancePropertiesDialog(MapEditor editor) {
        super(editor.getAppFrame(), DIALOG_TITLE, false);
        this.editor = editor;

        tableModel.addTableModelListener(this);

        init();
        pack();
        setLocationRelativeTo(getOwner());
    }

    private Properties getPropertiesAt(Point p) {
        MapLayer ml = editor.getCurrentLayer();
        if (!(ml instanceof TileLayer)) {
            return null;
        }

        return ((TileLayer) ml).getTileInstancePropertiesAt(p.x, p.y);
    }

    private void setPropertiesAt(Point point, Properties properties) {
        MapLayer ml = editor.getCurrentLayer();
        if (!(ml instanceof TileLayer)) {
            return;
        }

        ((TileLayer) ml).setTileInstancePropertiesAt(point.x, point.y,
                properties);
    }

    private void init() {
        propertiesTable = new JTable(tableModel);
        JScrollPane propScrollPane = new JScrollPane(propertiesTable);
        propScrollPane.setPreferredSize(new Dimension(200, 150));

        JButton applyButton = new JButton(APPLY_BUTTON);
        applyButton.setToolTipText(APPLY_TOOLTIP);
        JButton deleteButton = new JButton(Resources.getIcon("icon/gnome-delete.png"));
        deleteButton.setToolTipText(DELETE_BUTTON);

        JPanel user = new VerticalStaticJPanel();
        user.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        user.setLayout(new BoxLayout(user, BoxLayout.X_AXIS));
        user.add(Box.createGlue());
        user.add(Box.createRigidArea(new Dimension(5, 0)));
        user.add(deleteButton);

        JPanel buttons = new VerticalStaticJPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(applyButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(propScrollPane);
        mainPanel.add(user);
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(applyButton);

        //create actionlisteners
        applyButton.addActionListener(actionEvent -> buildPropertiesAndApply());

        deleteButton.addActionListener(actionEvent -> deleteSelected());
    }

    public void setSelection(SelectionLayer selection) {
        // Start off fresh...
        //Properties mergedProperties = new Properties();
        mergedProperties.clear();
        propertiesCoordinates.clear();

        // Get all properties of all selected tiles...
        MapLayer ml = editor.getCurrentLayer();
        if (ml instanceof TileLayer) {
            TileLayer tl = (TileLayer) ml;
            Rectangle r = selection.getSelectedAreaBounds();
            int maxJ = (int) (r.getY() + r.getHeight());
            int maxI = (int) (r.getX() + r.getWidth());

            // todo: BL - Why are tiles checked on null? Surely whether a tile
            // todo: is null or not has nothing to do with whether you can place
            // todo: a property as a certain location?
            for (int j = (int) r.getY(); j < maxJ; j++) {
                for (int i = (int) r.getX(); i < maxI; i++) {
                    Tile t = selection.getTileAt(i, j);
                    if (t != null) {
                        propertiesCoordinates.add(new Point(i, j));
                    }
                }
            }

            if (!propertiesCoordinates.isEmpty()) {
                // Start with properties of first tile instance
                Point point = propertiesCoordinates.get(0);
                Properties p = tl.getTileInstancePropertiesAt(point.x, point.y);

                if (p != null) {
                    mergedProperties.putAll(p);

                    for (int i = 1; i < propertiesCoordinates.size(); i++) {
                        // Merge the other properties...
                        point = propertiesCoordinates.get(i);
                        p = tl.getTileInstancePropertiesAt(point.x, point.y);

                        if (p != null) {
                            for (Enumeration<Object> e = mergedProperties.keys(); e.hasMoreElements(); ) {
                                // We only care for properties that are already "known"...
                                String key = (String) e.nextElement();
                                String val = mergedProperties.getProperty(key);
                                String mval = p.getProperty(key);

                                if (mval == null) {
                                    // Drop non-common properties
                                    mergedProperties.remove(key);
                                } else if (!mval.equals(val)) {
                                    // Hide non-common values
                                    mergedProperties.setProperty(key, "?");
                                }
                            }
                        } else {
                            mergedProperties.clear();
                            break;
                        }
                    }
                }
            }
        }

        tableModel.setProperties(mergedProperties);
    }


    private void buildPropertiesAndApply() {
        // Make sure there is no active cell editor anymore
        TableCellEditor cellEditor = propertiesTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }

        // Apply possibly changed properties.
        applyPropertiesToTiles();
    }


    private void deleteFromSelectedTiles(String key) {
        for (Point point : propertiesCoordinates) {
            Properties p = getPropertiesAt(point);
            if (p != null) p.remove(key);
        }
    }

    private void deleteSelected() {
        int total = propertiesTable.getSelectedRowCount();
        Object[] keys = new Object[total];
        int[] selRows = propertiesTable.getSelectedRows();

        for (int i = 0; i < total; i++) {
            keys[i] = propertiesTable.getValueAt(selRows[i], 0);
        }

        for (int i = 0; i < total; i++) {
            if (keys[i] != null) {
                tableModel.remove(keys[i]);
                deleteFromSelectedTiles((String) keys[i]);
            }
        }
    }

    private void applyPropertiesToTiles() {
        Properties properties = tableModel.getProperties();

        // First delete all Properties that were previously selected
        for (Point point : propertiesCoordinates) {
            Properties tp = getPropertiesAt(point);
            if (tp != null) {
                for (Enumeration<Object> e = mergedProperties.keys();
                     e.hasMoreElements(); ) {

                    String key = (String) e.nextElement();
                    String val = (String) properties.get(key);

                    if (!"?".equals(val)) {
                        // Property was removed or has a valid new value
                        tp.remove(key);
                    }
                }
            }
        }

        // Now update all selected Properties with the
        // new data
        for (Point point : propertiesCoordinates) {
            Properties tp = getPropertiesAt(point);
            if (tp == null) {
                tp = new Properties();
                setPropertiesAt(point, tp);
            }

            for (Enumeration<Object> e = properties.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                String val = properties.getProperty(key);
                if (!"?".equals(val)) {
                    tp.setProperty(key, val);
                }
            }
        }
    }

    public void tableChanged(TableModelEvent e) {
        applyPropertiesToTiles();
    }
}
