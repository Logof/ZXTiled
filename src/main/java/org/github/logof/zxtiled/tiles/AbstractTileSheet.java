package org.github.logof.zxtiled.tiles;

import org.github.logof.zxtiled.core.AbstractTile;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTileSheet {

    protected static final int TILE_WEIGHT = 16;
    protected static final int TILE_HEIGHT = 16;
    protected static final int TILE_SHEET_WEIGHT = 256;
    protected static final int TILE_SHEET_HEIGHT = 48;

    protected AbstractTile primaryBackgroundTile;
    protected List<AbstractTile> backgroundTiles = new ArrayList<>();
    protected AbstractTile pushableTile;
    protected AbstractTile lockTile;
    protected AbstractTile lifeTile;
    protected AbstractTile collectableTile;
    protected AbstractTile keyTile;
    protected AbstractTile alternativeBackgroundTile;
    protected List<AbstractTile> shadingBackgroundTiles = new ArrayList<>();

    protected BufferedImage rawTileSheet;

    abstract void splitTileSheet();

    protected BufferedImage makeTransparentImage(BufferedImage i)
    {
        return i;
    }
}
