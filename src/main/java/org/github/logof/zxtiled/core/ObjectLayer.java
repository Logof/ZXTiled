package org.github.logof.zxtiled.core;

import org.github.logof.zxtiled.mapeditor.Constants;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * A layer containing {@link MapObject map objects}.
 */
public class ObjectLayer extends MapLayer {
    private LinkedList<MapObject> objects = new LinkedList<>();

    /**
     * Default constructor.
     */
    public ObjectLayer() {
    }

    /**
     * @param tileMap the map this object group is part of
     */
    public ObjectLayer(TileMap tileMap) {
        super(tileMap);
    }

    /**
     * Creates an object group that is part of the given map and has the given
     * origin.
     *
     * @param tileMap   the map this object group is part of
     * @param origX the x origin of this layer
     * @param origY the y origin of this layer
     */
    public ObjectLayer(TileMap tileMap, int origX, int origY) {
        super(tileMap);
        setBounds(new Rectangle(origX, origY, 0, 0));
    }

    /**
     * Creates an object group with a given area. The size of area is
     * irrelevant, just its origin.
     *
     * @param area the area of the object group
     */
    public ObjectLayer(Rectangle area) {
        super(area);
    }

    /**
     * @see MapLayer#mirror(int)
     */
    public void mirror(int dir) {
        // TODO: Implement mirroring an object group
    }

    public void mergeOnto(MapLayer other) {
        // TODO: Implement merging with another object group
    }

    public void maskedMergeOnto(MapLayer other, Area mask) {
        // TODO: Figure out what object group should do with this method
    }

    public void copyFrom(MapLayer other) {
        // TODO: Implement copying from another object group (same as merging)
    }

    public void maskedCopyFrom(MapLayer other, Area mask) {
        // TODO: Figure out what object group should do with this method
    }

    @Override
    public void copyTo(MapLayer other) {
        super.copyTo(other);
        // TODO: Implement copying to another object group (same as merging)
    }

    /**
     * @see MapLayer#resize(int, int, int, int)
     */
    public void resize(int width, int height, int dx, int dy) {
        setBounds(new Rectangle(dx, dy, width, height));
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public Object clone() throws CloneNotSupportedException {
        ObjectLayer clone = (ObjectLayer) super.clone();
        clone.objects = new LinkedList<>();
        for (MapObject object : objects) {
            final MapObject objectClone = (MapObject) object.clone();
            clone.objects.add(objectClone);
            objectClone.setObjectLayer(clone);
        }
        return clone;
    }

    /**
     * @deprecated
     */
    public MapLayer createDiff(MapLayer ml) {
        return null;
    }

    public boolean addObject(MapObject mapObject) {
        if (checkAbilityCreateObject(mapObject)) {
            objects.add(mapObject);
            mapObject.setObjectLayer(this);
            return true;
        }
        return false;
    }

    public void removeObject(MapObject mapObject) {
        objects.remove(mapObject);
        mapObject.setObjectLayer(null);
    }

    public Iterator<MapObject> getObjects() {
        return objects.iterator();
    }

    // There can be 3 objects on one screen
    private boolean checkAbilityCreateObject(MapObject mapObject) {
        /*if (mapObject.getType().equals("playerStart")) {
            return objects.stream().noneMatch(object -> object.getType().equals("playerStart"));
        }

        if (mapObject.getType().equals("playerFinish")) {
            return objects.stream().noneMatch(object -> object.getType().equals("playerFinish"));
        }*/

        return objects.stream().filter(object -> object.getScreenNumber() == mapObject.getScreenNumber()).count() < 3;
    }

    /**
     * Finds the objects on this layer that intersect the given Rectangle.
     * The order in which the objects are returned is not guaranteed
     *
     * @param rect Rectangle in layer pixel coordinates.
     * @return objects that intersect the given rectangle
     */
    public MapObject[] findObjectsByOutline(Rectangle rect) {
        // FIXME: iterating over all objects is potentially very slow
        // there's room for optimization here by using some sort of space
        // partitioning for object storage
        Vector<MapObject> result = new Vector<>();
        Line2D l0 = new Line2D.Float();
        Line2D l1 = new Line2D.Float();
        Line2D l2 = new Line2D.Float();
        Line2D l3 = new Line2D.Float();
        for (MapObject obj : objects) {
            Rectangle b = obj.getBounds();
            float x0 = b.x;
            float y0 = b.y;
            float x1 = b.x + b.width;
            float y1 = b.y + b.height;
            l0.setLine(x0, y0, x1, y0);
            l1.setLine(x1, y0, x1, y1);
            l2.setLine(x1, y1, x0, y1);
            l3.setLine(x0, y1, x0, y0);
            if (l0.intersects(rect) || l1.intersects(rect) || l2.intersects(rect) || l3.intersects(rect))
                result.add(obj);
        }
        return result.toArray(new MapObject[result.size()]);
    }

    /**
     * Finds objects that are contained in the given rectangle
     *
     * @param rect Rectangle given in layer pixel coordinates that marks the
     *             area that is searched for objects
     * @return an array of MapObjects that are fully contained within the given
     * rectangle.
     */
    public MapObject[] findObjects(Rectangle rect) {
        Vector<MapObject> result = new Vector<>();
        for (MapObject o : objects) {
            if (rect.contains(o.getBounds()))
                result.add(o);
        }
        return result.toArray(new MapObject[result.size()]);
    }

    // This method will work at any zoom level, provided you provide the correct zoom factor. It also adds a one pixel buffer (that doesn't change with zoom).
    public MapObject getObjectNear(int x, int y, double zoom) {
        Rectangle2D mouse = new Rectangle2D.Double(x - zoom - 1, y - zoom - 1, 2 * zoom + 1, 2 * zoom + 1);
        Shape shape;

        for (MapObject obj : objects) {
            if (obj.getWidth() == 0 && obj.getHeight() == 0) {
                shape = new Ellipse2D.Double(obj.getX() * zoom, obj.getY() * zoom, 10 * zoom, 10 * zoom);
            } else {
                shape = new Rectangle2D.Double(obj.getX() + bounds.x * Constants.TILE_WIDTH,
                        obj.getY() + bounds.y * Constants.TILE_HEIGHT,
                        obj.getWidth() > 0 ? obj.getWidth() : zoom,
                        obj.getHeight() > 0 ? obj.getHeight() : zoom);
            }

            if (shape.intersects(mouse)) {
                return obj;
            }
        }

        return null;
    }
}
