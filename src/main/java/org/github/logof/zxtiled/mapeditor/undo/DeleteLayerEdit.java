/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.undo;

import org.github.logof.zxtiled.core.Map;
import org.github.logof.zxtiled.core.MapLayer;
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

    private final Map map;
    private final int index;
    private MapLayer layer = null;

    public DeleteLayerEdit(MapEditor editor, Map map, int index) {
        this.editor = editor;
        this.map = map;
        this.index = index;
        this.layer = map.getLayer(index);
    }

    public void undo() throws CannotUndoException {
        assert layer != null;
        super.undo();
        map.insertLayer(index, layer);
        if (editor.getCurrentLayerIndex() >= map.getTotalLayers())
            editor.setCurrentLayerIndex(map.getTotalLayers() - 1);
        layer = null;
    }

    public void redo() throws CannotRedoException {
        assert layer == null;
        super.redo();
        layer = map.getLayer(index);
        map.removeLayer(index);
    }

    public String getPresentationName() {
        return Resources.getString("action.layer.delete.name");
    }


}
