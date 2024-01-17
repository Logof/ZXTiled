package org.github.logof.zxtiled.mapeditor.listener;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.core.ObjectGroup;
import org.github.logof.zxtiled.core.PointerStateManager;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.brush.BrushException;
import org.github.logof.zxtiled.mapeditor.brush.CustomBrush;
import org.github.logof.zxtiled.mapeditor.brush.LayerInvisibleBrushException;
import org.github.logof.zxtiled.mapeditor.brush.LayerLockedBrushException;
import org.github.logof.zxtiled.mapeditor.dialogs.ObjectDialog;
import org.github.logof.zxtiled.mapeditor.enums.PointerStateEnum;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import org.github.logof.zxtiled.mapeditor.undo.AddObjectEdit;
import org.github.logof.zxtiled.mapeditor.undo.MapLayerEdit;
import org.github.logof.zxtiled.mapeditor.undo.MoveLayerEdit;
import org.github.logof.zxtiled.mapeditor.undo.MoveObjectEdit;
import org.github.logof.zxtiled.mapeditor.undo.RemoveObjectEdit;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Area;

import static org.github.logof.zxtiled.mapeditor.enums.PointerStateEnum.PS_PAINT;
import static org.github.logof.zxtiled.view.MapView.ZOOM_NORMAL_SIZE;

public class MapEditorMouseListener implements MouseListener,
                                               MouseMotionListener,
                                               MouseWheelListener {

    private int mouseButton;
    private boolean bMouseIsDown;
    private boolean bMouseIsDragging;
    private Point moveDist;
    private Point mousePressLocation;
    private Point mouseInitialPressLocation;
    private Point mouseInitialScreenLocation;
    private Point mouseLastPixelLocation;

    private final MapEditor mapEditor;

    public MapEditorMouseListener(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }


    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        MapLayer mapLayer = mapEditor.getCurrentLayer();

        Point tile = mapEditor.getMapView().screenToTileCoords(mapLayer, mouseEvent.getX(), mouseEvent.getY());
        mouseButton = mouseEvent.getButton();
        bMouseIsDown = true;
        bMouseIsDragging = false;
        mousePressLocation = mapEditor.getMapView().screenToTileCoords(mapLayer, mouseEvent.getX(), mouseEvent.getY());
        mouseInitialPressLocation = mousePressLocation;

        if (mouseButton == MouseEvent.BUTTON2 ||
                (mouseButton == MouseEvent.BUTTON1 &&
                        (mouseEvent.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) != 0)) {
            // Remember screen location for scrolling with middle mouse button
            mouseInitialScreenLocation = new Point(mouseEvent.getX(), mouseEvent.getY());
        } else if (mouseButton == MouseEvent.BUTTON1) {
            switch (PointerStateManager.getCurrentPointerState()) {
                case PS_PAINT:
                    if (mapLayer instanceof TileLayer) {
                        mapEditor.getCurrentBrush().startPaint(mapEditor.getCurrentTileMap(), tile.x, tile.y,
                                mouseButton, mapEditor.getCurrentLayerIndex());
                    }
                case PS_ERASE:
                case PS_POUR:
                    mapEditor.setPaintEdit(
                            new MapLayerEdit(mapLayer, mapEditor.createLayerCopy(mapLayer), null));
                    break;
                default:
            }
        }

        if (PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_MARQUEE) {
            boolean contains = mapEditor.getMarqueeSelection() != null && mapEditor.getMarqueeSelection()
                                                                                   .getSelectedArea()
                                                                                   .contains(tile.x, tile.y);

            if (mapEditor.getMarqueeSelection() == null && !contains) {
                mapEditor.setMarqueeSelection(new SelectionLayer(mapEditor.getCurrentLayer()));
                mapEditor.getCurrentTileMap().addLayerSpecial(mapEditor.getMarqueeSelection());
            } else if (mapEditor.getMarqueeSelection() != null && mouseEvent.getModifiers() == InputEvent.BUTTON1_MASK) {
                mapEditor.getCurrentTileMap().removeLayerSpecial(mapEditor.getMarqueeSelection());
                if (contains) {
                    mapEditor.setMarqueeSelection(null);
                } else {
                    mapEditor.setMarqueeSelection(new SelectionLayer(mapEditor.getCurrentLayer()));
                    mapEditor.getCurrentTileMap().addLayerSpecial(mapEditor.getMarqueeSelection());
                }
            }
        } else if (PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_MOVE) {
            // Initialize move distance to (0, 0)
            moveDist = new Point(0, 0);
        }

        doMouse(mouseEvent);
    }

    private void doMouse(MouseEvent event) {
        if (mapEditor.getCurrentTileMap() == null || mapEditor.getCurrentLayerIndex() < 0) {
            return;
        }

        MapLayer layer = mapEditor.getCurrentLayer();
        if (layer == null) {
            return;
        }

        Point tile = mapEditor.getMapView().screenToTileCoords(layer, event.getX(), event.getY());

        if (mouseButton == MouseEvent.BUTTON3) {
            if (layer instanceof TileLayer) {
                if (!bMouseIsDragging) {
                    // Click event is sent before the drag event
                    // so this one always happens
                    Tile newTile = ((TileLayer) layer).getTileAt(tile.x, tile.y);
                    mapEditor.setCurrentTile(newTile);
                } else if (PointerStateManager.getCurrentPointerState() == PS_PAINT) {
                    // In case we are dragging to create a custom brush, let
                    // the user know where we are creating it from
                    if (mapEditor.getMarqueeSelection() == null) {
                        mapEditor.setMarqueeSelection(new SelectionLayer(layer));
                        mapEditor.getCurrentTileMap().addLayerSpecial(mapEditor.getMarqueeSelection());
                    }

                    Point limp = mouseInitialPressLocation;
                    Rectangle oldArea =
                            mapEditor.getMarqueeSelection().getSelectedAreaBounds();
                    int minx = Math.min(limp.x, tile.x);
                    int miny = Math.min(limp.y, tile.y);

                    Rectangle selRect = new Rectangle(
                            minx, miny,
                            (Math.max(limp.x, tile.x) - minx) + 1,
                            (Math.max(limp.y, tile.y) - miny) + 1);

                    mapEditor.getMarqueeSelection().selectRegion(selRect);
                    if (oldArea != null) {
                        oldArea.add(mapEditor.getMarqueeSelection().getSelectedAreaBounds());
                        mapEditor.getMapView().repaintRegion(layer, oldArea);
                    }
                }
            } else if (layer instanceof ObjectGroup && !bMouseIsDragging) {
                // Get the object on this location and display the relative options dialog
                ObjectGroup group = (ObjectGroup) layer;
                Point pos = mapEditor.getMapView().screenToPixelCoords(
                        layer, event.getX(), event.getY());
                MapObject obj = group.getObjectNear(pos.x, pos.y, mapEditor.getMapView().getZoom());
                if (obj != null) {
                    ObjectDialog od = new ObjectDialog(mapEditor.getAppFrame(), obj, mapEditor.getUndoSupport());
                    od.getProps();
                }
            }
        } else if (mouseButton == MouseEvent.BUTTON2 ||
                (mouseButton == MouseEvent.BUTTON1 &&
                        (event.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) != 0)) {
            // Scroll with middle mouse button
            int dx = event.getX() - mouseInitialScreenLocation.x;
            int dy = event.getY() - mouseInitialScreenLocation.y;
            JViewport mapViewPort = mapEditor.getMapScrollPane().getViewport();
            Point currentPosition = mapViewPort.getViewPosition();
            mouseInitialScreenLocation = new Point(
                    event.getX() - dx,
                    event.getY() - dy);

            Point newPosition = new Point(
                    currentPosition.x - dx,
                    currentPosition.y - dy);

            // Take into account map boundaries in order to prevent
            // scrolling past them
            int maxX = java.lang.Math.max(0, mapEditor.getMapView().getWidth() - mapViewPort.getWidth());
            int maxY = java.lang.Math.max(0, mapEditor.getMapView().getHeight() - mapViewPort.getHeight());
            newPosition.x = Math.min(maxX, Math.max(0, newPosition.x));
            newPosition.y = Math.min(maxY, Math.max(0, newPosition.y));

            mapViewPort.setViewPosition(newPosition);
        } else if (mouseButton == MouseEvent.BUTTON1) {
            switch (PointerStateManager.getCurrentPointerState()) {
                case PS_PAINT:
                    mapEditor.getPaintEdit().setPresentationName(Constants.TOOL_PAINT);
                    if (layer instanceof TileLayer) {
                        try {
                            mapEditor.getMapView().repaintRegion(
                                    layer, mapEditor.getCurrentBrush().doPaint(tile.x, tile.y));
                            mapEditor.getStatusBar().getStatusLabel().clearText();
                        } catch (LayerLockedBrushException llx) {
                            mapEditor.getStatusBar().getStatusLabel()
                                     .setErrorText(Constants.STATUS_PAINT_ERROR_LAYER_LOCKED);
                        } catch (LayerInvisibleBrushException llx) {
                            mapEditor.getStatusBar().getStatusLabel()
                                     .setErrorText(Constants.STATUS_PAINT_ERROR_LAYER_INVISIBLE);
                        } catch (BrushException bx) {
                            mapEditor.getStatusBar().getStatusLabel()
                                     .setErrorText(Constants.STATUS_PAINT_ERROR_GENERAL);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case PS_ERASE:
                    mapEditor.getPaintEdit().setPresentationName(Constants.TOOL_ERASE);
                    if (layer instanceof TileLayer) {
                        ((TileLayer) layer).setTileAt(tile.x, tile.y, null);
                        mapEditor.getMapView().repaintRegion(layer, new Rectangle(
                                tile.x, tile.y, 1, 1));
                    }
                    break;
                case PS_POUR:
                    mapEditor.setPaintEdit(null);
                    if (layer instanceof TileLayer) {
                        TileLayer tileLayer = (TileLayer) layer;
                        Tile oldTile = tileLayer.getTileAt(tile.x, tile.y);
                        mapEditor.pour(tileLayer, tile.x, tile.y, mapEditor.getCurrentTile(), oldTile);
                        mapEditor.getMapView().repaint();
                    }
                    break;
                case PS_EYED:
                    if (layer instanceof TileLayer) {
                        TileLayer tileLayer = (TileLayer) layer;
                        Tile newTile = tileLayer.getTileAt(tile.x, tile.y);
                        mapEditor.setCurrentTile(newTile);
                    }
                    break;
                case PS_MOVE: {
                    Point translation = new Point(
                            tile.x - mousePressLocation.x,
                            tile.y - mousePressLocation.y);

                    layer.translate(translation.x, translation.y);
                    moveDist.translate(translation.x, translation.y);
                    mapEditor.getMapView().repaint();
                    mapEditor.getStatusBar().getStatusLabel()
                             .setInfoText(String.format(Constants.STATUS_LAYER_MOVED_FORMAT, layer.getBounds().x, layer.getBounds().y));
                    break;
                }
                case PS_MARQUEE:
                    if (!(layer instanceof TileLayer)) {
                        break;
                    }
                    if (mapEditor.getMarqueeSelection() != null) {
                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea =
                                mapEditor.getMarqueeSelection().getSelectedAreaBounds();
                        int minx = Math.min(limp.x, tile.x);
                        int miny = Math.min(limp.y, tile.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny,
                                (Math.max(limp.x, tile.x) - minx) + 1,
                                (Math.max(limp.y, tile.y) - miny) + 1);

                        if (event.isShiftDown()) {
                            mapEditor.getMarqueeSelection().add(new Area(selRect));
                        } else if (event.isControlDown()) {
                            mapEditor.getMarqueeSelection().subtract(new Area(selRect));
                        } else {
                            mapEditor.getMarqueeSelection().selectRegion(selRect);
                        }
                        if (oldArea != null) {
                            oldArea.add(
                                    mapEditor.getMarqueeSelection().getSelectedAreaBounds());
                            mapEditor.getMapView().repaintRegion(layer, oldArea);
                        }
                    }
                    break;
                case PS_ADD_OBJ:
                    if (layer instanceof ObjectGroup) {
                        if (mapEditor.getMarqueeSelection() == null) {
                            mapEditor.setMarqueeSelection(new SelectionLayer(mapEditor.getCurrentLayer()));
                            mapEditor.getCurrentTileMap().addLayerSpecial(mapEditor.getMarqueeSelection());
                        }

                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea =
                                mapEditor.getMarqueeSelection().getSelectedAreaBounds();
                        int minx = Math.min(limp.x, tile.x);
                        int miny = Math.min(limp.y, tile.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny,
                                (Math.max(limp.x, tile.x) - minx) + 1,
                                (Math.max(limp.y, tile.y) - miny) + 1);

                        mapEditor.getMarqueeSelection().selectRegion(selRect);
                        if (oldArea != null) {
                            oldArea.add(mapEditor.getMarqueeSelection().getSelectedAreaBounds());
                            mapEditor.getMapView().repaintRegion(layer, oldArea);
                        }
                    }
                    break;
                case PS_REMOVE_OBJ:
                    if (layer instanceof ObjectGroup) {
                        ObjectGroup group = (ObjectGroup) layer;
                        Point pos = mapEditor.getMapView().screenToPixelCoords(
                                layer, event.getX(), event.getY());
                        MapObject obj = group.getObjectNear(pos.x, pos.y, mapEditor.getMapView().getZoom());
                        if (obj != null) {
                            mapEditor.getUndoSupport().postEdit(new RemoveObjectEdit(group, obj));
                            group.removeObject(obj);
                            // TODO: repaint only affected area
                            mapEditor.getMapView().repaint();
                        }
                    }
                    break;
                case PS_MOVE_OBJ:
                    if (layer instanceof ObjectGroup) {
                        Point pos = mapEditor.getMapView().screenToPixelCoords(
                                layer, event.getX(), event.getY());
                        if (mapEditor.getCurrentObject() == null) {
                            ObjectGroup group = (ObjectGroup) layer;
                            mapEditor.setCurrentObject(group.getObjectNear(pos.x, pos.y, mapEditor.getMapView()
                                                                                                  .getZoom()));
                            if (mapEditor.getCurrentObject() == null) { // No object to move
                                break;
                            }
                            mouseLastPixelLocation = pos;
                            moveDist = new Point(0, 0);
                            break;
                        }
                        Point translation = new Point(
                                pos.x - mouseLastPixelLocation.x,
                                pos.y - mouseLastPixelLocation.y);
                        mapEditor.getCurrentObject().translate(translation.x, translation.y);
                        moveDist.translate(translation.x, translation.y);
                        mouseLastPixelLocation = pos;
                        mapEditor.getMapView().repaint();
                    }
                    break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        final MapLayer layer = mapEditor.getCurrentLayer();
        final Point limp = mouseInitialPressLocation;

        if (PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_MOVE) {
            if (layer != null && (moveDist.x != 0 || moveDist.y != 0)) {
                mapEditor.getUndoSupport().postEdit(new MoveLayerEdit(layer, moveDist));
            }
        } else if (PointerStateManager.getCurrentPointerState() == PS_PAINT) {
            if (layer instanceof TileLayer) {
                mapEditor.getCurrentBrush().endPaint();
            }
        } else if (PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_MOVE_OBJ) {
            if (layer instanceof ObjectGroup && mapEditor.getCurrentObject() != null &&
                    (moveDist.x != 0 || moveDist.y != 0)) {
                mapEditor.getUndoSupport().postEdit(
                        new MoveObjectEdit(mapEditor.getCurrentObject(), moveDist));
            }
        }

        if (PointerStateManager.getCurrentPointerState() == PS_PAINT ||
                PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_ADD_OBJ) {
            Point tile = mapEditor.getMapView().screenToTileCoords(
                    layer, mouseEvent.getX(), mouseEvent.getY());
            int minx = Math.min(limp.x, tile.x);
            int miny = Math.min(limp.y, tile.y);

            Rectangle bounds = new Rectangle(
                    minx, miny,
                    (Math.max(limp.x, tile.x) - minx) + 1,
                    (Math.max(limp.y, tile.y) - miny) + 1);

            // STAMP
            if (mouseButton == MouseEvent.BUTTON3 &&
                    layer instanceof TileLayer) {

                // Right mouse button dragged: create and set custom brush
                MapLayer cl = mapEditor.getCurrentLayer();
                TileLayer brushLayer = new TileLayer(bounds, cl.getTileWidth(), cl.getTileHeight());
                brushLayer.copyFrom(mapEditor.getCurrentLayer());
                brushLayer.setOffset(tile.x - (int) bounds.getWidth() / 2,
                        tile.y - (int) bounds.getHeight() / 2);

                // Do a quick check to make sure the selection is not empty
                if (brushLayer.isEmpty()) {
                    JOptionPane.showMessageDialog(mapEditor.getAppFrame(),
                            Resources.getString("dialog.selection.empty"),
                            Resources.getString("dialog.selection.empty"),
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    mapEditor.setBrush(new CustomBrush(brushLayer));
                    mapEditor.getCursorHighlight().setOffset(
                            tile.x - (int) bounds.getWidth() / 2,
                            tile.y - (int) bounds.getHeight() / 2);
                }
            } else if (mouseButton == MouseEvent.BUTTON1 &&
                    layer instanceof ObjectGroup) {
                // TODO: Fix this to use pixels in the first place
                // (with optional snap to grid)
                int w = mapEditor.getCurrentTileMap().getTileWidth();
                int h = mapEditor.getCurrentTileMap().getTileHeight();
                MapObject object = new MapObject(
                        bounds.x * w,
                        bounds.y * h,
                        bounds.width * w,
                        bounds.height * h);
                ObjectGroup group = (ObjectGroup) layer;
                mapEditor.getUndoSupport().postEdit(new AddObjectEdit(group, object));
                group.addObject(object);
                mapEditor.getMapView().repaint();
            }

            //get rid of any visible marquee
            if (mapEditor.getMarqueeSelection() != null) {
                mapEditor.getCurrentTileMap().removeLayerSpecial(mapEditor.getMarqueeSelection());
                mapEditor.setMarqueeSelection(null);
            }
        }

        if (mapEditor.getPaintEdit() != null) {
            if (layer != null) {
                try {
                    MapLayer endLayer = mapEditor.getPaintEdit().getStart().createDiff(layer);
                    mapEditor.getPaintEdit().end(endLayer);
                    mapEditor.getUndoSupport().postEdit(mapEditor.getPaintEdit());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mapEditor.setPaintEdit(null);
        }

        mapEditor.setCurrentObject(null);

        mouseButton = MouseEvent.NOBUTTON;
        bMouseIsDown = false;
        bMouseIsDragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        mapEditor.getStatusBar().getTilePositionLabel().setText(" ");
        mapEditor.updateCursorHighlight(null);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        bMouseIsDragging = true;

        doMouse(mouseEvent);

        MapLayer layer = mapEditor.getCurrentLayer();
        mousePressLocation = mapEditor.getMapView().screenToTileCoords(layer, mouseEvent.getX(), mouseEvent.getY());
        Point tile = mapEditor.getMapView().screenToTileCoords(layer, mouseEvent.getX(), mouseEvent.getY());

        mapEditor.updateTileCoordsLabel(tile);
        mapEditor.updateCursorHighlight(tile);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        // Update state of mouse buttons
        bMouseIsDown = mouseEvent.getButton() != MouseEvent.NOBUTTON;
        if (bMouseIsDown) {
            doMouse(mouseEvent);
        }

        Point tile = null;
        MapLayer currentLayer = mapEditor.getCurrentLayer();
        if (currentLayer != null) {
            tile = mapEditor.getMapView()
                            .screenToTileCoords(mapEditor.getCurrentLayer(), mouseEvent.getX(), mouseEvent.getY());
        }
        mapEditor.updateTileCoordsLabel(tile);
        mapEditor.updateCursorHighlight(tile);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        // only accept events from mapView. Ctrl key must be pressed as well.
        if (mouseWheelEvent.getComponent() != mapEditor.getMapView()) {
            return;
        }

        // if we're not processing event, pass it in to mapView's scroll pane
        if ((mouseWheelEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
            for (MouseWheelListener wheelListener : mapEditor.getMapScrollPane().getMouseWheelListeners())
                wheelListener.mouseWheelMoved(mouseWheelEvent);
            return;
        }
        int amount = mouseWheelEvent.getWheelRotation();
        mapEditor.getMapView().setZoomLevel(mapEditor.getMapView().getZoomLevel() - amount);
        MapEditorAction.zoomNormalAction.setEnabled(mapEditor.getMapView().getZoomLevel() != ZOOM_NORMAL_SIZE);
    }
}
