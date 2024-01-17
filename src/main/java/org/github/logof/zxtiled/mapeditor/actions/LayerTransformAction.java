package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.ObjectGroup;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.undo.MapLayerEdit;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class LayerTransformAction extends AbstractAction {
    private final int transform;
    private final MapEditor mapEditor;

    public LayerTransformAction(MapEditor mapEditor, int transform) {
        this.transform = transform;
        this.mapEditor = mapEditor;

        switch (transform) {
            case MapLayer.ROTATE_90:
                putValue(NAME, Resources.getString("action.layer.transform.rotate90.name"));
                putValue(SHORT_DESCRIPTION,
                        Resources.getString("action.layer.transform.rotate90.tooltip"));
                putValue(SMALL_ICON,
                        Resources.getIcon("icon/gimp-rotate-90-16.png"));
                break;
            case MapLayer.ROTATE_180:
                putValue(NAME, Resources.getString("action.layer.transform.rotate180.name"));
                putValue(SHORT_DESCRIPTION,
                        Resources.getString("action.layer.transform.rotate180.tooltip"));
                putValue(SMALL_ICON,
                        Resources.getIcon("icon/gimp-rotate-180-16.png"));
                break;
            case MapLayer.ROTATE_270:
                putValue(NAME, Resources.getString("action.layer.transform.rotate270.name"));
                putValue(SHORT_DESCRIPTION,
                        Resources.getString("action.layer.transform.rotate270.tooltip"));
                putValue(SMALL_ICON,
                        Resources.getIcon("icon/gimp-rotate-270-16.png"));
                break;
            case MapLayer.MIRROR_VERTICAL:
                putValue(NAME, Resources.getString("action.layer.transform.vertical.name"));
                putValue(SHORT_DESCRIPTION, Resources.getString("action.layer.transform.vertical.tooltip"));
                putValue(SMALL_ICON,
                        Resources.getIcon("icon/gimp-flip-vertical-16.png"));
                break;
            case MapLayer.MIRROR_HORIZONTAL:
                putValue(NAME, Resources.getString("action.layer.transform.horizontal.name"));
                putValue(SHORT_DESCRIPTION, Resources.getString("action.layer.transform.horizontal.tooltip"));
                putValue(SMALL_ICON,
                        Resources.getIcon("icon/gimp-flip-horizontal-16.png"));
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        MapLayer currentLayer = mapEditor.getCurrentLayer();
        MapLayer layer = currentLayer;
        MapLayerEdit transEdit;
        transEdit = new MapLayerEdit(currentLayer, MapEditor.createLayerCopy(currentLayer));

        if (mapEditor.getMarqueeSelection() != null) {
            if (currentLayer instanceof TileLayer) {
                layer = new TileLayer(
                        mapEditor.getMarqueeSelection()
                                 .getSelectedAreaBounds(), currentLayer.getTileWidth(), currentLayer.getTileHeight());
            } else if (currentLayer instanceof ObjectGroup) {
                layer = new ObjectGroup(
                        mapEditor.getMarqueeSelection().getSelectedAreaBounds());
            }
            layer.setMap(mapEditor.getCurrentTileMap());
            layer.maskedCopyFrom(
                    currentLayer,
                    mapEditor.getMarqueeSelection().getSelectedArea());
        }

        switch (transform) {
            case MapLayer.ROTATE_90:
            case MapLayer.ROTATE_180:
            case MapLayer.ROTATE_270:
                transEdit.setPresentationName("Rotate");
                layer.rotate(transform);
                //if(mapEditor.getMarqueeSelection() != null) mapEditor.getMarqueeSelection().rotate(transform);
                break;
            case MapLayer.MIRROR_VERTICAL:
                transEdit.setPresentationName("Vertical Flip");
                layer.mirror(MapLayer.MIRROR_VERTICAL);
                //if(mapEditor.getMarqueeSelection() != null) mapEditor.getMarqueeSelection().mirror(transform);
                break;
            case MapLayer.MIRROR_HORIZONTAL:
                transEdit.setPresentationName("Horizontal Flip");
                layer.mirror(MapLayer.MIRROR_HORIZONTAL);
                //if(mapEditor.getMarqueeSelection() != null) mapEditor.getMarqueeSelection().mirror(transform);
                break;
        }

        if (mapEditor.getMarqueeSelection() != null) {
            layer.mergeOnto(currentLayer);
        }

        transEdit.end(MapEditor.createLayerCopy(currentLayer));
        mapEditor.getUndoSupport().postEdit(transEdit);
        mapEditor.getMapView().repaint();
    }

    @Override
    public boolean accept(Object sender) {
        return super.accept(sender);
    }
}
