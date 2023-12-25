package org.github.logof.zxtiled.core;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

/**
 * Responsible for managing the layout of the current frame. It takes care of
 * setting up and clearing the entire frame when a new map is created or loaded.
 */
public class LayoutManager 
{
	private MapperFrame parentFrame;
	private MapPanel mapPanel;
	private JScrollPane tilePanelScrollPane;
	private JScrollPane objectPanelScrollPane;
	private JScrollPane mapPanelScrollPane;
	
	private final static float TILE_PANEL_RATIO = 0.25f;
	
	/**
	 * Constructs a new LayoutManager
	 * @param parentFrame - The MapperFrame to assign this layout manager to
	 * @param mapPanel - The MapPanel to assign this layout manager to
	 */
	public LayoutManager(MapperFrame parentFrame, MapPanel mapPanel)
	{
		this.parentFrame = parentFrame;
		this.mapPanel = mapPanel;
	}
	
	/**
	 * Sets the layout manager's components to the new incoming parameters
	 * @param parentFrame - The new MapperFrame to assign the layout manager to
	 * @param mapPanel - The new MapPanel to assign the layout manager to
	 */
	public void setNewInfo(MapperFrame parentFrame, MapPanel mapPanel)
	{
		this.parentFrame = parentFrame;
		this.mapPanel = mapPanel;
	}
	
	/**
	 * Creates all of the scroll panes and adds the
	 * components that are set in the layout manager to them
	 */
	public void initializeLayout()
	{	
		// Create the JScrollPanes for the tile and map panels
		tilePanelScrollPane = new JScrollPane(mapPanel.getTilePanel());
		tilePanelScrollPane.setMinimumSize(new Dimension((int)(parentFrame.getWidth() * TILE_PANEL_RATIO), parentFrame.getHeight() / 2));
		tilePanelScrollPane.setBorder(BorderFactory.createTitledBorder("Tiles"));
		
		objectPanelScrollPane = new JScrollPane(mapPanel.getObjectPanel());
		objectPanelScrollPane.setMinimumSize(new Dimension((int)(parentFrame.getWidth() * TILE_PANEL_RATIO), parentFrame.getHeight() / 2));
		objectPanelScrollPane.setBorder(BorderFactory.createTitledBorder("Objects"));
				
		// Create the map panel
		mapPanelScrollPane = new JScrollPane(mapPanel);
		mapPanelScrollPane.setBorder(BorderFactory.createTitledBorder("Map"));
				
		// Declare the constraints for the GridBagLayout
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
				
		// Add the MapPanel to the frame
		parentFrame.add(mapPanelScrollPane, gridBagConstraints);
		parentFrame.validate();
				
		// Update constraints for the object panel
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
				
		parentFrame.add(objectPanelScrollPane, gridBagConstraints);
		parentFrame.validate();
		
		// Update constraints for the tile panel
		gridBagConstraints.gridy = 0;
		
		// Add the TilePanel to the frame
		parentFrame.add(tilePanelScrollPane, gridBagConstraints);
		parentFrame.validate();
		
		// Enable map dependent menu buttons
		parentFrame.getMenuPanel().setMapDependentItems(true);
		parentFrame.repaint();
	}
	
	/**
	 * Clears the existing layout so that a new map can be loaded
	 */
	public void clearExistingLayout()
	{
		objectPanelScrollPane.removeAll();
		tilePanelScrollPane.removeAll();
		mapPanelScrollPane.removeAll();
		
		parentFrame.remove(objectPanelScrollPane);
		parentFrame.remove(tilePanelScrollPane);
		parentFrame.remove(mapPanelScrollPane);
	}
}