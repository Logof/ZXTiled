package org.github.logof.zxtiled.core;
import lombok.Getter;
import java.awt.image.BufferedImage;

/**
 * Represents a single, abstract tile. Only for use with the TileSheet class.
 */
@Getter
public class AbstractTile 
{
	private final int width;
	private final int height;
	private final BufferedImage texture;
	
	public static final int DEFAULT_TILE_HEIGHT = 16;
	public static final int DEFAULT_TILE_WIDTH = 16;
	
	/**
	 * Default Tile constructor. Probably won't be used anyway,
	 * but it initializes the Tile to default values.
	 */
	public AbstractTile()
	{
		height = DEFAULT_TILE_HEIGHT;
		width = DEFAULT_TILE_WIDTH;
		texture = new BufferedImage(DEFAULT_TILE_HEIGHT, DEFAULT_TILE_WIDTH, BufferedImage.TYPE_INT_RGB);
	}
	
	/**
	 * Tile constructor which initializes the Tile's image to the 
	 * explicit parameter and sets the width, height, x, and y to default values.
	 * @param image - The BufferedImage which will represent the Tile's graphic
	 */
	public AbstractTile(BufferedImage image)
	{
		height = image.getHeight();
		width = image.getWidth();
		texture = image;
	}
	             
	/**
	 * Gets the BufferedImage graphic associated with this Tile
	 * @return The BufferedImage that the Tile represents
	 */
	public BufferedImage getImage()
	{
		return texture;
	}

}