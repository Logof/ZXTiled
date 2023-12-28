package org.github.logof.zxtiled.core;

import lombok.Getter;
import org.github.logof.zxtiled.entity.Tile;
import org.github.logof.zxtiled.util.SpringUtilities;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel which shows all of the currently loaded tiles to select from.
 */
@Getter
public class TilePanel extends JPanel
{
	private int selectedTileIndex;
	private final TileSheet tileSheet;
	private final List<Tile> tiles = new ArrayList<>();
	private final boolean isObjectPanel;
	private MapPanel associatedMapPanel;
	
	private final static int PADDING = 1;
	
	/**
	 * This class represents the panel which holds all of the
	 * tiles which can be selected to draw with
	 * @param tileSheet - The TileSheet to be displayed
	 */
	public TilePanel(TileSheet tileSheet, boolean isObjectSheet)
	{	
		// Set initial attributes
		this.tileSheet = tileSheet;
		this.selectedTileIndex = 0;
		this.isObjectPanel = isObjectSheet;

		setLayout(new SpringLayout());

		int idIndex = 0;

		int tempSize;

		// Assign the size and List<Tile> based on whether this is a object sheet or not
		if (isObjectSheet) {
			setBackground(Color.LIGHT_GRAY);
			tempSize = tileSheet.objects.size();
			
			Tile iconLabel = null;
			try {
				Image eraseTile = ImageIO.read(new File("img/eraseTile.png"));
				eraseTile = eraseTile.getScaledInstance(tileSheet.getWidthOfTiles(), tileSheet.getHeightOfTiles(), 0);
				iconLabel = new Tile(eraseTile, this, -1);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			tiles.add(iconLabel);
			idIndex++;
			add(iconLabel);
		} else {
			tempSize = tileSheet.tiles.size();
			setBackground(Color.BLACK);
		}

		for(AbstractTile tile: (isObjectSheet ? tileSheet.objects : tileSheet.tiles)) {
			Tile iconLabel = new Tile(tile.getImage(), this, idIndex);
			tiles.add(iconLabel);
			idIndex++;
			add(iconLabel);
		}
		
		// Determine amount of tile columns needed
		int tileColumns = calcTileColumns(200, tileSheet.getWidthOfTiles(), PADDING);
		
		// Determine amount of blank tiles needed
		int blankTiles = calcBlankTiles(tempSize, tileColumns);
		
		// Add the blank tiles to the panel
		for (int i = 0; i < blankTiles; i++)
			add(new JLabel());
		
		// Convert the layout into a compact grid
		SpringUtilities.makeCompactGrid(this, // parent
				tempSize/tileColumns+1, tileColumns, //tempSize / tileColumns + 1, tileColumns,
                0, 0,  // initX, initY
                PADDING, PADDING); // xPad, yPad
	}
	
	/**
	 * Calculates the number of blank tiles needed to  
	 * fill in the tile panel
	 * @param tileCount - total number of tiles
	 * @return - number of blank tiles needed
	 */
	public int calcBlankTiles(int tileCount, int tileColumns)
	{
		return ((tileCount / tileColumns + 1) * tileColumns) - tileCount;
	}
	
	/**
	 * Calculates the number of tile columns
	 * @param tilePanelWidth - Total width of the tile panel
	 * @param tileWidth - Width of tiles
	 * @param padding - Amount of pixels used for padding
	 * @return - number of tile columns
	 */
	public int calcTileColumns(int tilePanelWidth, int tileWidth, int padding)
	{
		return tilePanelWidth/(tileWidth + padding);
	}
	
	/**
	 * Gets the MapPanel which this TilePanel is associated with
	 * @return
	 */
	public MapPanel getMapPanel()
	{
		return associatedMapPanel;
	}
	

	/**
	 * Sets the selected tile to the incoming 
	 * index and clears the previously selected one
	 * @param index - The index to change the selected tile to
	 */
	public void setSelectedTile(int index)
	{
		tiles.get(selectedTileIndex).setSelected(false);
		selectedTileIndex = index;
		tiles.get(index).setSelected(true);
	}

	/**
	 * Gets the image of the specified tile
	 * @param id - The ID of the tile you want to retrieve the image from
	 * @return The image of the specified tile
	 */
	public Image getTileImage(int id)
	{
		return tiles.get(id).getImage();
	}
	
	/**
	 * Sets the MapPanel which this TilePanel is associated with
	 * @param panel - The MapPanel which this TilePanel is associated with
	 */
	public void setMapPanel(MapPanel panel)
	{
		associatedMapPanel = panel;
	}
	
	/**
	 * Tells whether or not this is an object panel
	 * @return True if this is an object panel; False if it's a tile panel
	 */
	public boolean isObjectPanel()
	{
		return isObjectPanel;
	}
}