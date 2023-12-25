package org.github.logof.zxtiled.core;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.zxtiled.menu.MenuPanel;

/**
 * The JFrame which serves as the main GUI for the application.
 */
@Setter
@Getter
public class MapperFrame extends JFrame
{
	public static final int FRAME_WIDTH = 1024;
	public static final int FRAME_HEIGHT = 640;
	public static final int MINIMUM_WIDTH = 400;
	public static final int MINIMUM_HEIGHT = 300;

	private TilePanel tilePanel;
	private TilePanel objectPanel;
	private MapPanel mapPanel;
	private LayoutManager layoutManager;
	private MenuPanel menuPanel;
	
	/**
	 * Creates the main JFrame which is the core GUI for the map editor
	 */
	public MapperFrame()
	{
		// Setup the JFrame
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
		setTitle("Tile Mapper v0.7");
		setLayout(new GridBagLayout());
		
		// Set the IconImage
		try 
		{
			setIconImage(ImageIO.read(new File("img/blankTile.png")));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// Construct menu
		MenuPanel menuPanel = new MenuPanel(this);
		setJMenuBar(menuPanel);
		
		// Load the default map
		MapIO.loadProjectAsXML("example_map.tmf", this);
	}
	
	/**
	 * Creates a modal dialog used for creating a new map
	 */
	public void createNewMapDialog()
	{
		new NewMapDialog(this);
	}

	/**
	 * Displays the splash screen
	 */
	public void showSplash()
	{
		Splash splash = new Splash(this);
		splash.run();
	}
}