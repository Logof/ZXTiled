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

package org.github.logof.zxtiled.mapeditor.util;

import org.github.logof.zxtiled.core.MapChangeAdapter;
import org.github.logof.zxtiled.core.MapChangeListener;
import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.MultilayerPlane;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.event.MapChangedEvent;
import org.github.logof.zxtiled.core.event.MapLayerChangeEvent;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.table.AbstractTableModel;
import java.util.Objects;

/**
 * The model used to display the layer stack.
 */
public class LayerTableModel extends AbstractTableModel {
    private static final String[] columnNames = {
            Resources.getString("dialog.main.locked.column"),
            Resources.getString("dialog.main.show.column"),
            Resources.getString("dialog.main.layername.column")
    };
    private MultilayerPlane map;
    private final MapChangeListener listener = new MapChangeAdapter() {
        @Override
        public void layerChanged(MapChangedEvent e, MapLayerChangeEvent changeEvent) {
            if (e.getMap() != map) {
                return;
            }
            int row = getRowCount() - e.getLayerIndex() - 1;
            fireTableRowsUpdated(row, row);
        }
    };


    public LayerTableModel() {
    }

    public LayerTableModel(MultilayerPlane map) {
        this.map = null;
        setMap(map);
    }

    public void setMap(MultilayerPlane map) {
        if (this.map == map) {
            return;
        }
        if (this.map != null) {
            try {
                ((TileMap) map).removeMapChangeListener(listener);
            } catch (ClassCastException ignored) {

            }
        }
        this.map = map;
        if (Objects.nonNull(map)) {
            ((TileMap) map).addMapChangeListener(listener);
        }
        fireTableDataChanged();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public int getRowCount() {
        if (map == null) {
            return 0;
        }
        return 2;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Class getColumnClass(int col) {
        switch (col) {
            case 0:
            case 1:
                return Boolean.class;
            case 2:
                return String.class;
            default:
                return null;
        }
    }

    public Object getValueAt(int row, int col) {
        MapLayer layer = map.getLayer(getRowCount() - row - 1);

        if (layer != null) {
            if (col == 0) {
                return layer.getLocked() || !layer.isVisible();
            } else if (col == 1) {
                return layer.isVisible();
            } else if (col == 2) {
                return layer.getName();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean isCellEditable(int row, int col) {
        MapLayer layer = map.getLayer(getRowCount() - row - 1);

        return !(col == 0 && layer != null && !layer.isVisible());
    }

    public void setValueAt(Object value, int row, int col) {
        MapLayer layer = map.getLayer(getRowCount() - row - 1);
        if (layer != null) {
            if (col == 0) {
                layer.setLocked((Boolean) value);
            } else if (col == 1) {
                layer.setVisible((Boolean) value);
            } else if (col == 2) {
                layer.setName(value.toString());
            }
            fireTableCellUpdated(row, col);
        }
    }
}
