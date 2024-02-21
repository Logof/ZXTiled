package org.github.logof.zxtiled.core;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.zxtiled.mapeditor.Constants;
import java.util.Properties;
import java.util.Vector;

/**
 * The Map class is the focal point of the <code>tiled.core</code> package.
 * This class also handles notifing listeners if there is a change to any layer
 * or object contained by the map.
 *
 * @version $Id$
 */
@Getter
public class SpriteMap extends MultilayerPlane {
    private final Vector<Tileset> tilesets;
    @Setter
    private MapTypeEnum mapType = MapTypeEnum.MAP_SIDE_SCROLLED;
    @Setter
    private Properties properties;
    @Setter
    private String filename;

    /**
     * @param width  the map width in tiles.
     * @param height the map height in tiles.
     */
    public SpriteMap(int width, int height) {
        super(width, height);
        properties = new Properties();
        tilesets = new Vector<>();
    }

    /**
     * Returns width of map in tiles.
     *
     * @return int
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Returns height of map in tiles.
     *
     * @return int
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Returns wether the given tile coordinates fall within the map
     * boundaries.
     *
     * @param x The tile-space x-coordinate
     * @param y The tile-space y-coordinate
     * @return <code>true</code> if the point is within the map boundaries,
     * <code>false</code> otherwise
     */
    public boolean contains(int x, int y) {
        return x >= 0 && y >= 0 && x < bounds.width && y < bounds.height;
    }


    /**
     * Returns string describing the map. The form is <code>Map[width x height
     * x layers][tileWidth x tileHeight]</code>, for example <code>
     * Map[64x64x2][24x24]</code>.
     *
     * @return string describing map
     */
    public String toString() {
        return "Sprites[" + bounds.width + "x" + bounds.height + "x" +
                getTotalLayers() + "][" + Constants.TILE_WIDTH + "x" + Constants.TILE_HEIGHT + "]";
    }

}
