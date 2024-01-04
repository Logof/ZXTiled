package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.Map;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.util.TiledFileFilter;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ExportAction extends SaveAsAction {

    protected MapEditor editor;

    public ExportAction(MapEditor editor) {
        super(editor);
        putValue(NAME, Resources.getString("action.map.export.name"));
        putValue(SHORT_DESCRIPTION, Resources.getString("action.map.export.tooltip"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control E"));
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Map currentMap = editor.getCurrentMap();
        String filePath = currentMap.getFilename();

        if (filePath != null) {
            int dotIndex = filePath.lastIndexOf('.');
            String extension = (dotIndex == -1) ? "" : filePath.substring(dotIndex);

            if (!extension.equalsIgnoreCase(".h")) {
                filePath = filePath.replace(extension, ".h");
            }
            saveFile(new TiledFileFilter(TiledFileFilter.FILTER_EXT), filePath);
        } else {
            super.actionPerformed(e);
        }
    }
}
