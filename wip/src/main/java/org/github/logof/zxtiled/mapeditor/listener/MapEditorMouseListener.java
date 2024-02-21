package org.github.logof.zxtiled.mapeditor.listener;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.ObjectLayer;
import org.github.logof.zxtiled.core.PointerStateManager;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.core.objects.HotspotObject;
import org.github.logof.zxtiled.core.objects.MapObject;
import org.github.logof.zxtiled.core.objects.MovingObject;
import org.github.logof.zxtiled.core.objects.PlayerFinishObject;
import org.github.logof.zxtiled.core.objects.PlayerStartObject;
import org.github.logof.zxtiled.exception.BrushException;
import org.github.logof.zxtiled.exception.LayerInvisibleBrushException;
import org.github.logof.zxtiled.exception.LayerLockedBrushException;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.enums.MapObjectGlobalTypeEnum;
import org.github.logof.zxtiled.mapeditor.enums.PointerStateEnum;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.ObjectDialog;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import org.github.logof.zxtiled.mapeditor.undo.AddObjectEdit;
import org.github.logof.zxtiled.mapeditor.undo.MapLayerEdit;
import org.github.logof.zxtiled.mapeditor.undo.MoveObjectEdit;
import org.github.logof.zxtiled.mapeditor.undo.RemoveObjectEdit;
import org.github.logof.zxtiled.util.CoordinateUtil;
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
    private boolean mouseIsDown;
    private boolean mouseIsDragging;
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

        Point point = mapEditor.getMapView().screenToTileCoordinates(mapLayer, mouseEvent.getX(), mouseEvent.getY());
        mouseButton = mouseEvent.getButton();
        mouseIsDown = true;
        mouseIsDragging = false;
        mousePressLocation = mapEditor.getMapView()
                                      .screenToTileCoordinates(mapLayer, mouseEvent.getX(), mouseEvent.getY());
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
                        mapEditor.getCurrentBrush().startPaint(mapEditor.getCurrentTileMap(), point.x, point.y,
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
                                                                                   .contains(point.x, point.y);

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

        Point coordinates = mapEditor.getMapView().screenToTileCoordinates(layer, event.getX(), event.getY());

        if (mouseButton == MouseEvent.BUTTON3) {
            if (layer instanceof TileLayer) {
                if (!mouseIsDragging) {
                    // Событие щелчка отправляется перед событием перетаскивания, поэтому оно происходит всегда.
                    Tile newTile = ((TileLayer) layer).getTileAt(coordinates.x, coordinates.y);
                    mapEditor.setCurrentTile(newTile);
                } else if (PointerStateManager.getCurrentPointerState() == PS_PAINT) {
                    // Если мы перетаскиваем, чтобы создать собственную кисть, сообщите пользователю, откуда мы ее создаем.
                    if (mapEditor.getMarqueeSelection() == null) {
                        mapEditor.setMarqueeSelection(new SelectionLayer(layer));
                        mapEditor.getCurrentTileMap().addLayerSpecial(mapEditor.getMarqueeSelection());
                    }

                    Point limp = mouseInitialPressLocation;
                    Rectangle oldArea =
                            mapEditor.getMarqueeSelection().getSelectedAreaBounds();
                    int minx = Math.min(limp.x, coordinates.x);
                    int miny = Math.min(limp.y, coordinates.y);

                    Rectangle selRect = new Rectangle(
                            minx, miny,
                            (Math.max(limp.x, coordinates.x) - minx) + 1,
                            (Math.max(limp.y, coordinates.y) - miny) + 1);

                    mapEditor.getMarqueeSelection().selectRegion(selRect);
                    if (oldArea != null) {
                        oldArea.add(mapEditor.getMarqueeSelection().getSelectedAreaBounds());
                        mapEditor.getMapView().repaintRegion(layer, oldArea);
                    }
                }
            } else if (layer instanceof ObjectLayer && !mouseIsDragging) {
                // Получите объект в этом месте и отобразите диалоговое окно относительных параметров.
                ObjectLayer objectLayer = (ObjectLayer) layer;

                double zoom = mapEditor.getMapView().getZoom();
                Point position = CoordinateUtil.zoomedScreenToPixelCoordinates(event.getX(), event.getY(), zoom);

                MapObject object = objectLayer.getObjectNear(position.x, position.y, mapEditor.getMapView().getZoom());
                if (object != null) {
                    /*Vector<Tileset> tileset = Objects.nonNull(mapEditor.getCurrentTileMap().getTilesets())
                            ? mapEditor.getCurrentTileMap().getTilesets() : null;
                    OpenDialogs.openObjectDialog(mapEditor.getAppFrame(), object, tileset);*/
                    ObjectDialog objectDialog = new ObjectDialog(mapEditor.getAppFrame(), object, mapEditor.getUndoSupport());
                    objectDialog.getProps();
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

            // Take into account map boundaries in order to prevent scrolling past them
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
                            mapEditor.getMapView()
                                     .repaintRegion(layer, mapEditor.getCurrentBrush()
                                                                    .doPaint(coordinates.x, coordinates.y));
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
                        ((TileLayer) layer).setTileAt(coordinates.x, coordinates.y, null);
                        mapEditor.getMapView().repaintRegion(layer, new Rectangle(
                                coordinates.x, coordinates.y, 1, 1));
                    }
                    break;
                case PS_POUR:
                    mapEditor.setPaintEdit(null);
                    if (layer instanceof TileLayer) {
                        TileLayer tileLayer = (TileLayer) layer;
                        Tile oldTile = tileLayer.getTileAt(coordinates.x, coordinates.y);
                        mapEditor.pour(tileLayer, coordinates.x, coordinates.y, mapEditor.getCurrentTile(), oldTile);
                        mapEditor.getMapView().repaint();
                    }
                    break;
                case PS_MARQUEE:
                    if (!(layer instanceof TileLayer)) {
                        break;
                    }
                    if (mapEditor.getMarqueeSelection() != null) {
                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea =
                                mapEditor.getMarqueeSelection().getSelectedAreaBounds();
                        int minx = Math.min(limp.x, coordinates.x);
                        int miny = Math.min(limp.y, coordinates.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny,
                                (Math.max(limp.x, coordinates.x) - minx) + 1,
                                (Math.max(limp.y, coordinates.y) - miny) + 1);

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
                case PS_ADD_HOTSPOT:
                    if (layer instanceof ObjectLayer) {
                        if (mapEditor.getMarqueeSelection() == null) {
                            mapEditor.setMarqueeSelection(new SelectionLayer(mapEditor.getCurrentLayer()));
                            mapEditor.getCurrentTileMap().addLayerSpecial(mapEditor.getMarqueeSelection());
                        }

                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea = mapEditor.getMarqueeSelection().getSelectedAreaBounds();
                        int minx = Math.min(limp.x, coordinates.x);
                        int miny = Math.min(limp.y, coordinates.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny,
                                (Math.max(limp.x, coordinates.x) - minx) + 1,
                                (Math.max(limp.y, coordinates.y) - miny) + 1);

                        mapEditor.getMarqueeSelection().selectRegion(selRect);
                        if (oldArea != null) {
                            oldArea.add(mapEditor.getMarqueeSelection().getSelectedAreaBounds());
                            mapEditor.getMapView().repaintMapObject(oldArea);
                        }
                    }
                    break;
                case PS_REMOVE_OBJ:
                    if (layer instanceof ObjectLayer) {
                        ObjectLayer objectLayer = (ObjectLayer) layer;
                        Point pos = CoordinateUtil.zoomedScreenToPixelCoordinates(event.getX(), event.getY(), mapEditor.getMapView()
                                                                                                                       .getZoom());
                        MapObject obj = objectLayer.getObjectNear(pos.x, pos.y, mapEditor.getMapView().getZoom());
                        if (obj != null) {
                            mapEditor.getUndoSupport().postEdit(new RemoveObjectEdit(objectLayer, obj));
                            objectLayer.removeObject(obj);
                            // TODO: repaint only affected area
                            mapEditor.getMapView().repaint();
                        }
                    }
                    break;
                case PS_MOVE_OBJ:
                    if (layer instanceof ObjectLayer) {
                        Point point = CoordinateUtil.zoomedScreenToPixelCoordinates(event.getX(), event.getY(),
                                mapEditor.getMapView().getZoom());

                        if (mapEditor.getCurrentObject() == null) {
                            ObjectLayer group = (ObjectLayer) layer;
                            mapEditor.setCurrentObject(group.getObjectNear(point.x, point.y, mapEditor.getMapView()
                                                                                                      .getZoom()));
                            if (mapEditor.getCurrentObject() == null) { // No object to move
                                break;
                            }
                            mouseLastPixelLocation = point;
                            moveDist = new Point(0, 0);
                            break;
                        }
                        Point translation = new Point(
                                point.x - mouseLastPixelLocation.x,
                                point.y - mouseLastPixelLocation.y);
                        mapEditor.getCurrentObject().translate(translation.x, translation.y);
                        moveDist.translate(translation.x, translation.y);
                        mouseLastPixelLocation = point;
                        mapEditor.getMapView().repaint();
                    }
                    break;
                case PS_START_OBJECT: {
                    if (layer instanceof ObjectLayer) {
                        if (mapEditor.getMarqueeSelection() == null) {
                            mapEditor.setMarqueeSelection(new SelectionLayer(mapEditor.getCurrentLayer()));
                            mapEditor.getCurrentTileMap().addLayerSpecial(mapEditor.getMarqueeSelection());
                        }

                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea = mapEditor.getMarqueeSelection().getSelectedAreaBounds();
                        int minx = Math.min(limp.x, coordinates.x);
                        int miny = Math.min(limp.y, coordinates.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);

                        mapEditor.getMarqueeSelection().selectRegion(selRect);
                        if (oldArea != null) {
                            oldArea.add(mapEditor.getMarqueeSelection().getSelectedAreaBounds());
                            mapEditor.getMapView().repaintRegion(layer, oldArea);
                        }
                    }
                    break;
                }
                case PS_FINISH_OBJECT: {
                    if (layer instanceof ObjectLayer) {

                    }
                }
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        final MapLayer layer = mapEditor.getCurrentLayer();
        final Point limp = mouseInitialPressLocation;

        if (PointerStateManager.getCurrentPointerState() == PS_PAINT) {
            if (layer instanceof TileLayer) {
                mapEditor.getCurrentBrush().endPaint();
            }
        } else if (PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_MOVE_OBJ) {
            if (layer instanceof ObjectLayer && mapEditor.getCurrentObject() != null &&
                    (moveDist.x != 0 || moveDist.y != 0)) {
                mapEditor.getUndoSupport().postEdit(
                        new MoveObjectEdit(mapEditor.getCurrentObject(), moveDist));
            }
        }

        if (PointerStateManager.getCurrentPointerState() == PS_PAINT ||
                PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_ADD_OBJ ||
                PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_ADD_HOTSPOT) {
            Point point = mapEditor.getMapView().screenToTileCoordinates(
                    layer, mouseEvent.getX(), mouseEvent.getY());
            int minx = Math.min(limp.x, point.x);
            int miny = Math.min(limp.y, point.y);

            Rectangle bounds = new Rectangle(
                    minx, miny,
                    (Math.max(limp.x, point.x) - minx) + 1,
                    (Math.max(limp.y, point.y) - miny) + 1);

            // STAMP
            if (mouseButton == MouseEvent.BUTTON3 && layer instanceof TileLayer) {
                // Right mouse button dragged: create and set custom brush
                TileLayer brushLayer = new TileLayer(bounds);
                brushLayer.copyFrom(mapEditor.getCurrentLayer());
                brushLayer.setOffset(point.x - (int) bounds.getWidth() / 2,
                        point.y - (int) bounds.getHeight() / 2);

                // Do a quick check to make sure the selection is not empty
                if (brushLayer.isEmpty()) {
                    JOptionPane.showMessageDialog(mapEditor.getAppFrame(),
                            Resources.getString("dialog.selection.empty"),
                            Resources.getString("dialog.selection.empty"),
                            JOptionPane.WARNING_MESSAGE);
                }
            } else if (mouseButton == MouseEvent.BUTTON1 && layer instanceof ObjectLayer) {
                ObjectLayer objectLayer = (ObjectLayer) layer;

                MapObject object = createMapObject(mouseEvent,
                        PointerStateManager.getCurrentPointerState() == PointerStateEnum.PS_ADD_OBJ ?
                                MapObjectGlobalTypeEnum.ENEMY :
                                MapObjectGlobalTypeEnum.HOTSPOT);
                mapEditor.getUndoSupport().postEdit(new AddObjectEdit(objectLayer, object));

                if (objectLayer.addObject(object)) {
                    ObjectDialog objectDialog = new ObjectDialog(mapEditor.getAppFrame(), object, mapEditor.getUndoSupport());
                    objectDialog.getProps();
                    /*Vector<Tileset> tileset = Objects.nonNull(mapEditor.getCurrentTileMap().getTilesets())
                            ? mapEditor.getCurrentTileMap().getTilesets() : null;*/
                    //OpenDialogs.openObjectDialog(mapEditor.getAppFrame(), object, tileset);

                }
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
        mouseIsDown = false;
        mouseIsDragging = false;
    }

    private MapObject createMapObject(MouseEvent mouseEvent, MapObjectGlobalTypeEnum objectType) {
        int mapNumber = CoordinateUtil.mouseToScreenNumber(mouseEvent.getX(), mouseEvent.getY(),
                mapEditor.getMapView()
                         .getWidth() / (int) (Constants.SCREEN_WIDTH * Constants.TILE_WIDTH * mapEditor.getMapView()
                                                                                                       .getZoom()),
                mapEditor.getMapView().getZoom());
        switch (objectType) {
            case ENEMY:
                return new MovingObject(mouseInitialPressLocation.x, mouseInitialPressLocation.y, mapNumber);
            case HOTSPOT:
                return new HotspotObject(mouseInitialPressLocation.x, mouseInitialPressLocation.y, mapNumber);
            case PLAYER_START:
                return new PlayerStartObject(mouseInitialPressLocation.x, mouseInitialPressLocation.y, mapNumber);
            case PLAYER_FINISH:
                return new PlayerFinishObject(mouseInitialPressLocation.x, mouseInitialPressLocation.y, mapNumber);
            default:
                return null;
        }
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
        mouseIsDragging = true;

        doMouse(mouseEvent);

        MapLayer layer = mapEditor.getCurrentLayer();
        mousePressLocation = mapEditor.getMapView()
                                      .screenToTileCoordinates(layer, mouseEvent.getX(), mouseEvent.getY());
        Point tile = mapEditor.getMapView().screenToTileCoordinates(layer, mouseEvent.getX(), mouseEvent.getY());

        mapEditor.updateTileCoordinatesLabel(tile);
        mapEditor.updateCursorHighlight(tile);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        // Update state of mouse buttons
        mouseIsDown = mouseEvent.getButton() != MouseEvent.NOBUTTON;
        if (mouseIsDown) {
            doMouse(mouseEvent);
        }

        Point tile = null;
        MapLayer currentLayer = mapEditor.getCurrentLayer();
        if (currentLayer != null) {
            tile = mapEditor.getMapView()
                            .screenToTileCoordinates(mapEditor.getCurrentLayer(), mouseEvent.getX(), mouseEvent.getY());
        }
        mapEditor.updateTileCoordinatesLabel(tile);
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
