package org.github.logof.zxtiled.mapeditor.listener;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MapEditorComponentListener implements ComponentListener {
    private final MapEditor mapEditor;

    public MapEditorComponentListener(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        // This can currently only happen when the map changes size
        String s = (int) (mapEditor.getMapView().getZoom() * 100) + "%";
        mapEditor.getStatusBar().getZoomLabel().setText(s);

        // Restore the midpoint
        JViewport mapViewPort = mapEditor.getMapScrollPane().getViewport();
        Rectangle viewRect = mapViewPort.getViewRect();
        int absMidX = Math.max(0, Math.round(mapEditor.getRelativeMidX() * mapEditor.getMapView()
                                                                                    .getWidth()) - viewRect.width / 2);
        int absMidY = Math.max(0, Math.round(mapEditor.getRelativeMidY() * mapEditor.getMapView()
                                                                                    .getHeight()) - viewRect.height / 2);
        mapViewPort.setViewPosition(new Point(absMidX, absMidY));
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {

    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {

    }
}
