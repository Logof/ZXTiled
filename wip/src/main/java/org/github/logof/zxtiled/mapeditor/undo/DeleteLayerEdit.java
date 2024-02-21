/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.undo;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * @author upachler
 */
public class DeleteLayerEdit extends AbstractUndoableEdit {
    private final MapEditor editor;

    private final TileMap tileMap;
    private final int index;
    private MapLayer layer;

    public DeleteLayerEdit(MapEditor editor, TileMap tileMap, int index) {
        this.editor = editor;
        this.tileMap = tileMap;
        this.index = index;
        this.layer = tileMap.getLayer(index);
    }

    public void undo() throws CannotUndoException {
        assert layer != null;
        super.undo();
        tileMap.insertLayer(index, layer);
        if (editor.getCurrentLayerIndex() >= tileMap.getTotalLayers()) {
            editor.setCurrentLayerIndex(tileMap.getTotalLayers() - 1);
        }
        layer = null;
    }

    public void redo() throws CannotRedoException {
        assert layer == null;
        super.redo();
        layer = tileMap.getLayer(index);
        tileMap.removeLayer(index);
    }

    public String getPresentationName() {
        return Resources.getString("action.layer.delete.name");
    }


}
