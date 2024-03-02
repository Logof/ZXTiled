package org.github.logof.zxtiled.mapeditor.actions;

import lombok.SneakyThrows;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.io.c.HEnemsWriter;
import org.github.logof.zxtiled.io.c.HMapWriter;
import org.github.logof.zxtiled.io.c.HTilesetWriter;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.util.UnzipUtility;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ExportAction extends SaveAsAction {

    protected MapEditor editor;

    public ExportAction(MapEditor editor) {
        super(editor);
        putValue(NAME, Resources.getString("action.map.export.name"));
        putValue(SHORT_DESCRIPTION, Resources.getString("action.map.export.tooltip"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control E"));
        this.editor = editor;
    }

    @SneakyThrows
    @Override
    public void actionPerformed(ActionEvent e) {
        TileMap currentTileMap = editor.getCurrentTileMap();
        String filePath = currentTileMap.getFilename();

        if (Objects.nonNull(filePath)) {
            int dotIndex = filePath.lastIndexOf('.');
            String extension = (dotIndex == -1) ? "" : filePath.substring(dotIndex);
            String projectFolder = filePath.replace(extension, "");

            UnzipUtility.extractFiles(projectFolder);

            // Export
            // tilemap to file mapa.h
            HMapWriter.writeMap(editor.getCurrentTileMap(), projectFolder + "/dev/assets/mapa.h");
            // objects map to file enems.h
            HEnemsWriter.writeEnems(editor.getCurrentTileMap(), projectFolder + "/dev/assets/enems.h");
            // tileset to file tileset.h
            HTilesetWriter.writeTileset(editor.getCurrentTileMap()
                                              .getTilesets(), projectFolder + "/dev/assets/tileset.h");
        } else {
            super.actionPerformed(e);
        }
    }
}
