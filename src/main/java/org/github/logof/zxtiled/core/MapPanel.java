package org.github.logof.zxtiled.core;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.zxtiled.util.SpringUtilities;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This panel holds all of the MapTiles which can be drawn to. It 
 * displays the map that is currently being drawn on.
 */
@Getter
@Setter
public class MapPanel extends JPanel 
{
	private static final long serialVersionUID = 6810863879602201457L;
	private final MapperFrame parentFrame;
	private final TilePanel tilePanel;
    private final TilePanel objectPanel;
	private final int xTiles;
    private final int yTiles;
	private final List<MapTile> tiles;
	private final List<Integer> projectedTileIndexes;
	private final SpringLayout springLayout;
	private boolean objectPanelSelectedLast;
	private int drawCount;
	
	/**
	 * Constructs the panel which tiles are to be drawn to
	 * @param xTiles - The number of horizontal tiles
	 * @param yTiles - The number of vertical tiles
	 * @param tilePanel - The TilePanel to be that holds the tiles
	 */
	public MapPanel(MapperFrame parentFrame, int xTiles, int yTiles, TilePanel tilePanel, TilePanel objectPanel)
	{
		// Setup initial attributes
		this.parentFrame = parentFrame;
		tiles = new ArrayList<>();
		projectedTileIndexes = new ArrayList<>();
		this.tilePanel = tilePanel;
		this.objectPanel = objectPanel;
		this.xTiles = xTiles;
		this.yTiles = yTiles;
		
		objectPanelSelectedLast = false;
		drawCount = 1;

		springLayout = new SpringLayout();
		setLayout(springLayout);
		
		// Load the blank tile image and create a reference to a scaled version of it
		ImageIcon temp = new ImageIcon("img/blankTile.png");
		Image scaledIcon = temp.getImage().getScaledInstance(tilePanel.getTileSheet().getWidthOfTiles(), tilePanel.getTileSheet().getHeightOfTiles(), 0);
				
		// Add the blank tiles to the MapPanel
		for(int i = 0; i < xTiles * yTiles; i++)
		{
            temp.setImage(scaledIcon);
			MapTile tile = new MapTile(temp, this, i);
			
			tiles.add(tile);
			add(tile);
		}
		
		// Force the layout to become a compact grid
		SpringUtilities.makeGrid(this, //parent
                yTiles, xTiles,
                0, 0,  //initX, initY
                0, 0); //xPad, yPad
	}


	/**
	 * Gets all of the MapTiles stored in this map
	 * @return The ArrayList<MapTile> stored in this MapPanel
	 */
	public List<MapTile> getMapTiles()
	{
		return tiles;
	}

	/**
	 * Tells whether the object panel was the last panel selected
	 * @return True if object panel was last selected, false if tile panel was last selected
	 */
	public boolean objectPanelSelectedLast()
	{
		return objectPanelSelectedLast;
	}
	
	/**
	 * Gets the height of the currently displayed map in number of tiles
	 * @return The number of tiles high that this map panel displays
	 */
	public int getHeightInTiles()
	{
		return yTiles;
	}
	
	/**
	 * Gets the data about which tiles are collidable and which aren't
	 * @return The ArrayList<Byte> of collision layer data
	 */
	public List<Byte> getCollisionLayerData()
	{
		return tiles.stream().map(MapTile::getCollidable).collect(Collectors.toList());
	}
	
	/**
	 * Gets the object layer IDs of all of the MapTiles
	 * @return The id of the object tile drawn to the MapTile
	 */
	public List<Integer> getObjectLayerData()
	{
		return tiles.stream().map(MapTile::getObjectLayerId).collect(Collectors.toList());
	}
	
	public MapTile getTile(int index)
	{
		return tiles.get(index);
	}
	/**
	 * Gets the tile layer IDs of all of the MapTiles
	 * @return The id of the tile drawn to the MapTile
	 */
	public List<Integer> getTileLayerData()
	{
		return tiles.stream().map(MapTile::getTileLayerId).collect(Collectors.toList());
	}
	
	/**
	 * Used when loading maps, this sets the layer data for both the tile and object layers
	 * and then repaints them so that the loaded map can be shown
	 * @param tileLayer - The List<Integer> of tile data to set
	 * @param objectLayer - The List<Integer> of object data to set
	 */
	public void setLayerData(List<Integer> tileLayer, List<Integer> objectLayer, List<Byte> collisionLayer)
	{
		for (int i = 0; i < tiles.size(); i++)
		{
			MapTile currentTile = tiles.get(i);
			
			// Set the object and tile layer data
			currentTile.setObjectLayerId(objectLayer.get(i));
			currentTile.setTileLayerId(tileLayer.get(i));
			
			// Set the collision layer data
			currentTile.setCollidable(collisionLayer.get(i));
			
			// Redraw
			currentTile.repaint();
		}
	}
	
	/**
	 * Gets the width of the currently displayed map in number of tiles
	 * @return The number of tiles wide that this map panel displays
	 */
	public int getWidthInTiles()
	{
		return xTiles;
	}
	
	/**
	 * Gets the total number of tiles in the map panel
	 * @return The total number of tiles in the map panel.
	 */
	public int getTotalNumberOfTiles()
	{
		return tiles.size();
	}
	
	/**
	 * Tells whether or not the collision mode is enabled
	 * @return Whether or not collision mode is enabled
	 */
	public boolean collisionModeEnabled()
	{
		return parentFrame.getMenuPanel().getViewMenu().getCollisionMode();
	}
	
	/**
	 * Tells whether or not the object mode is enabled
	 * @return Whether or not object mode is enabled
	 */
	public boolean objectModeEnabled()
	{
		return parentFrame.getMenuPanel().getViewMenu().getObjectLayerMode();
	}
	
	/**
	 * Tells whether or not the tile mode is enabled
	 * @return Whether or not tile mode is enabled
	 */
	public boolean tileModeEnabled()
	{
		return parentFrame.getMenuPanel().getViewMenu().getTileLayerMode();
	}
	
	/**
	 * Tells whether or not the grid mode is enabled
	 * @return Whether or not grid mode is enabled
	 */
	public boolean gridModeEnabled()
	{
		return parentFrame.getMenuPanel().getViewMenu().getGridMode();	
	}
	
	/**
	 * Repaints all of the tiles on the map panel
	 */
	public void repaintAllTiles()
	{
		for(MapTile t : tiles) {
			t.repaint();
		}
	}
	
	/**
	 * Repaints all of the tiles that are projected to be drawn to,
	 * used with the multi-draw function
	 */
	public void repaintProjectedTiles()
	{
		for (int i : projectedTileIndexes)
			tiles.get(i).setHovered(false);
		for (int i : projectedTileIndexes)
			tiles.get(i).repaint();
	}
	
	/**
	 * Erases all projected indexes
	 */
	public void clearProjectedIndexes()
	{
		projectedTileIndexes.clear();
	}
	
	/**
	 * Adds an index to the projected indexes tracker
	 * @param index - The index to be added
	 */
	public void addToProjectedIndexes(int index)
	{
		projectedTileIndexes.add(index);
	}

}