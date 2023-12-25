package org.github.logof.zxtiled.tiles;

import org.github.logof.zxtiled.core.AbstractTile;
import org.github.logof.zxtiled.enums.TileTypeEnum;
import java.awt.image.BufferedImage;

public class TileSheet16 extends AbstractTileSheet {

    //TODO Add Automatic shading
    @Override
    void splitTileSheet() {
        // Tile 0 - background
        // Tiles from 1 to 13 can be whatever you want: background tiles, obstacles, platforms, killers...
        // Tile 14 = pushable tiles
        // Tile 15 (the last one) = door lock
        // Tile 16 = life recharge
        // Tile 17 = collectibles
        // Tile 18 = keys
        // Tile 19 = alternative background. To give some age to the screens, randomly, this tile will be painted
        //           from time to time instead of tile 0.
        // Automatic shading
        int xTiles = rawTileSheet.getWidth() / TILE_WEIGHT;
        int yTiles = rawTileSheet.getHeight() / TILE_HEIGHT;

        int lastTileNumber = 0;
        for (int y = 0; y < yTiles; y++)
        {
            for (int x = 0; x < xTiles; x++)
            {
                // The BufferedImage must support an alpha channel
                BufferedImage bufferedImage = new BufferedImage(TILE_WEIGHT, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

                bufferedImage.getGraphics().drawImage(rawTileSheet.getSubimage(x * TILE_WEIGHT,
                        y * TILE_HEIGHT,
                        TILE_WEIGHT,
                        TILE_HEIGHT), 0, 0, null);

                switch (TileTypeEnum.getTileTypeById(lastTileNumber)) {
                    case PRIMARY_BACKGROUND -> primaryBackgroundTile = new AbstractTile(bufferedImage);
                    case BACKGROUND -> backgroundTiles.add(new AbstractTile(bufferedImage));
                    case PUSH -> pushableTile = new AbstractTile(bufferedImage);
                    case LOCK -> lockTile = new AbstractTile(bufferedImage);
                    case LIFE -> lifeTile = new AbstractTile(bufferedImage);
                    case COLLECTABLE -> collectableTile = new AbstractTile(bufferedImage);
                    case KEY -> keyTile = new AbstractTile(bufferedImage);
                    case ALTERNATIVE_BACKGROUND -> alternativeBackgroundTile = new AbstractTile(bufferedImage);
                    case SHADOW -> shadingBackgroundTiles.add(new AbstractTile(bufferedImage));
                    default -> System.out.println("Не удалось определить тип тайла");
                }
                lastTileNumber +=1;
            }
        }
    }
}
