package org.github.logof.zxtiled.core;

import lombok.Getter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all of the information about the tile sheet that was loaded, such as
 * image loaded, tile width and height, and the transparent color associated with it
 */
@Getter
public class TileSheet {
	public List<AbstractTile> tiles;
	public List<AbstractTile> objects;
	private BufferedImage rawTileSheet;
	private static final int TILE_WIDTH = 16;
	private static final int TILE_HEIGHT = 16;
	private static final int TILE_VISIBLE_WIDTH = 32;
	private static final int TILE_VISIBLE_HEIGHT = 32;

	public TileSheet(BufferedImage spriteSheet) {
		tiles = new ArrayList<>();
		objects = new ArrayList<>();
		rawTileSheet = spriteSheet;
		splitTileSheet();
	}

	/**
	 * TileSheet constructor which is based on an incoming sprite sheet image
	 * file and custom height and width sizes for each Tile
	 *
	 * @param spriteSheetFile - The image file which contains all of the Tile images
	 */
	public TileSheet(File spriteSheetFile) {
		tiles = new ArrayList<>();
		objects = new ArrayList<>();
		try {
			rawTileSheet = ImageIO.read(spriteSheetFile);
		} catch (IOException e) {
			System.out.println("Error loading spritesheet.");
		}

		splitTileSheet();
	}

	/**
	 * Gets the width of tiles in the TileSheet
	 *
	 * @return The width of Tiles contained in the TileSheet
	 */
	public int getWidthOfTiles() {
		return TILE_WIDTH;
	}

	/**
	 * Gets the height of tiles in the TileSheet
	 *
	 * @return The height of Tiles contained in the TileSheet
	 */
	public int getHeightOfTiles() {
		return TILE_HEIGHT;
	}

	/**
	 * Gets the raw image that was initially passed into this TileSheet
	 *
	 * @return - The image used by this TileSheet before it was split up
	 */
	public BufferedImage getRawImage() {
		return rawTileSheet;
	}

	/**
	 * Determines whether the image is considered an object tile or not
	 * based on whether or not it contains a pixel which is the same
	 * color as the transparentColor field
	 *
	 * @param image - The image to be checked
	 * @return True if the image is an object tile, false otherwise
	 */
	private boolean checkObjectTile(BufferedImage image) {
		return false;
	}

	/**
	 * Converts all of the specified transparent color pixels into
	 * actually transparent pixels
	 *
	 * @param i - The image to convert
	 * @return The new transparent image
	 */
	private BufferedImage makeTransparentImage(BufferedImage i) {
		// Determine the r,g,b values of the transparent color
		// Check each pixel of the image
		return i;
	}

	/**
	 * Called at the end of each constructor,
	 * this method breaks up the raw sprite sheet image
	 * into individual Tiles and adds them to the TileSheet's
	 * ArrayList which stores the Tiles
	 */
	private void splitTileSheet() {
		int xTilesCount = rawTileSheet.getWidth() / TILE_WIDTH;
		int yTilesCount = rawTileSheet.getHeight() / TILE_HEIGHT;

		// Split the raw sprite sheet into individual tiles accordingly
		// And add them to the ArrayList of Tiles
		for (int y = 0; y < yTilesCount; y++) {
			for (int x = 0; x < xTilesCount; x++) {
				// The BufferedImage must support an alpha channel
				BufferedImage image = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

				image.getGraphics().drawImage(rawTileSheet.getSubimage(x * TILE_WIDTH, y
						* TILE_WIDTH, TILE_WIDTH, TILE_HEIGHT), 0, 0, null);

				if (checkObjectTile(image)) {
					objects.add(new AbstractTile(makeTransparentImage(image)));
				} else {
					tiles.add(new AbstractTile(image));
				}
			}
		}
	}
}