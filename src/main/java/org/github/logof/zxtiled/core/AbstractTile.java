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
	private final BufferedImage image;


	/**
	 * Tile constructor which initializes the Tile's image to the 
	 * explicit parameter and sets the width, height, x, and y to default values.
	 * @param image - The BufferedImage which will represent the Tile's graphic
	 */
	public AbstractTile(BufferedImage image) {
		this.height = image.getHeight();
		this.width = image.getWidth();
		this.image = image;
	}
}