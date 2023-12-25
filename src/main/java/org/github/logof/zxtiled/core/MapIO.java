package org.github.logof.zxtiled.core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.*;

import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;


/**
 * Handles loading and saving maps. Also handles Base64 encoding and decoding
 * for the tile sheet image.
 */
public class MapIO 
{
	/**
	 * Loads the .tmf file into the program and sets it up to be edited
	 * @param fileName - The file path to read the project from
	 * @param parentFrame - The MapperFrame in use
	 */
	public static void loadProjectAsXML(String fileName, MapperFrame parentFrame)
	{
		SAXBuilder parser = new SAXBuilder();
		
		try
		{
			Document doc = parser.build(new File(fileName));
			Element root = doc.getRootElement();
			
			// Get tilesheet_image
			Element tilesheet_image = root.getChild("tilesheet_image");
			String tileSheetOriginal = tilesheet_image.getText();
			
			// Get map width and height
			Element map_width = root.getChild("map_width");
			Element map_height = root.getChild("map_height");
			
			int mapWidth = Integer.parseInt(map_width.getText());
			int mapHeight = Integer.parseInt(map_height.getText());
			
			// Get tile width and height
			Element tile_width = root.getChild("tile_width");
			Element tile_height = root.getChild("tile_height");
			
			int tileWidth = Integer.parseInt(tile_width.getText());
			int tileHeight = Integer.parseInt(tile_height.getText());
			
			// Get transparent color
			Element transparent_color = root.getChild("transparent_color");
			
			Element eRed = transparent_color.getChild("red");
			Element eGreen = transparent_color.getChild("green");
			Element eBlue= transparent_color.getChild("blue");
			
			int red = Integer.parseInt(eRed.getText());
			int green = Integer.parseInt(eGreen.getText());
			int blue = Integer.parseInt(eBlue.getText());
			
			// Get tile, object, and collision layer data
			List<Integer> tilesLayerData = new ArrayList<>();
			List<Integer> objectsLayerData = new ArrayList<>();
			List<Byte> collisionLayerData = new ArrayList<>();
			
			List<Element> list = root.getChildren("tile");
			
			// Iterate through each tile element
            for (Element element : list) {
                // Get the value of each ID
                int tileLayerID = Integer.parseInt(element.getChild("tile_layer_id").getText());
                int objectLayerID = Integer.parseInt(element.getChild("object_layer_id").getText());
                byte collisionLayerID = Byte.parseByte(element.getChild("collision_layer_id").getText());

                // Add them to the list
                tilesLayerData.add(tileLayerID);
                objectsLayerData.add(objectLayerID);
                collisionLayerData.add(collisionLayerID);
            }
			
			// Convert Base64 string to the image
			byte[] imageBytes = Base64.getDecoder().decode(tileSheetOriginal);
			InputStream in = new ByteArrayInputStream(imageBytes);
			BufferedImage decodedImage = ImageIO.read(in);
						
			// Create the tile sheet from the variables that have been read
			TileSheet sheet = new TileSheet(decodedImage, tileWidth, tileHeight, new Color(red, green, blue));
						
			// Create the map, tile, and object panels					
			TilePanel tilePanel = new TilePanel(sheet, false);
			TilePanel objectPanel = new TilePanel(sheet, true);
			MapPanel mapPanel = new MapPanel(parentFrame, mapWidth, mapHeight, tilePanel, objectPanel);
						
			// Assign the panels to the main frame
			parentFrame.setTilePanel(tilePanel);
			parentFrame.setObjectPanel(objectPanel);
			parentFrame.setMapPanel(mapPanel);
						
			// Assign the map panel to the tile selection panels
			tilePanel.setMapPanel(mapPanel);
			objectPanel.setMapPanel(mapPanel);
						
			LayoutManager manager;
						
			// If a layout manager doesn't already exist, create one
			if (parentFrame.getLayoutManager() == null)
			{
				manager = new LayoutManager(parentFrame, mapPanel);
				parentFrame.setLayoutManager(manager);
			}
			
			// If one does exist, clear old layout and update it with the new info
			else
			{
				manager = parentFrame.getLayoutManager();
				manager.clearExistingLayout();
				manager.setNewInfo(parentFrame, mapPanel);
			}
						
			// Initialize the new layout
			manager.initializeLayout();
						
			// Set tiles and object data to the MapPanel
			mapPanel.setLayerData(tilesLayerData, objectsLayerData, collisionLayerData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Exports the currently open map to a .map file
	 * @param filePath - The file path to write the project to
	 * @param frame - The MapperFrame in use
	 */
	public static void exportProjectAsXML(String filePath, MapperFrame frame)
	{
		String xml =
                "<map>" +
                "   <tilesheet_image>" +
                "   </tilesheet_image>" +
                "   <map_width></map_width>" +
                "   <map_height></map_height>" +
                "   <tile_width></tile_width>" +
                "   <tile_height></tile_height>" +
                "   <transparent_color>" +
                "      <red></red>" +
                "      <green></green>" +
                "      <blue></blue>" +
                "   </transparent_color>" +
                "</map>";

        SAXBuilder builder = new SAXBuilder();
        try 
        {
        	
        	// Create XML document
            Document document = builder.build(new StringReader(xml));
            
            // Get root 
            Element map = document.getRootElement();
            
            // Calculate original tilesheet image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ImageIO.write(frame.getTilePanel().getTileSheet().getRawImage(), "png", baos);
    		byte[] encodedImage = Base64.getEncoder().encode(baos.toByteArray());
    		
    		// Store it
            Element tilesheet_image = map.getChild("tilesheet_image");
            tilesheet_image.setText(new String(encodedImage));

            // Calculate map width and height
            Element map_width = map.getChild("map_width");
            map_width.setText(frame.getMapPanel().getWidthInTiles() + "");
            
            Element map_height = map.getChild("map_height");
            map_height.setText(frame.getMapPanel().getHeightInTiles() + "");
            
            // Calculate tile width and height
            Element tile_width = map.getChild("tile_width");
            tile_width.setText(frame.getTilePanel().getTileSheet().getWidthOfTiles() + "");
            
            Element tile_height = map.getChild("tile_height");
            tile_height.setText(frame.getTilePanel().getTileSheet().getHeightOfTiles() + "");
            
            // Calculate transparent color
            Element transparent_color = map.getChild("transparent_color");
            Element red = transparent_color.getChild("red");
            Element green = transparent_color.getChild("green");
            Element blue = transparent_color.getChild("blue");
            
            Color transparent = frame.getTilePanel().getTileSheet().getTransparentColor();
            red.setText(transparent.getRed() + "");
            green.setText(transparent.getGreen() + "");
            blue.setText(transparent.getBlue() + "");
            
            // Get object and tile layer data
            List<Integer> tileIDs = frame.getMapPanel().getTileLayerData();
            List<Integer> objectIDs = frame.getMapPanel().getObjectLayerData();
            List<Byte> collisionIDs = frame.getMapPanel().getCollisionLayerData();
            
    		for(int i = 0; i < tileIDs.size(); i++)
    		{
    			// Create a tile element
    			Element tempTile = new Element("tile");
    			
    			// Tile layer data
    			Element tile_layer_id = new Element("tile_layer_id");
    			tile_layer_id.setText(tileIDs.get(i) + "");
    			
    			// Object layer data
    			Element object_layer_id = new Element("object_layer_id");
    			object_layer_id.setText(objectIDs.get(i) + "");
    			
    			// Collision layer data
    			Element collision_layer_id = new Element("collision_layer_id");
    			collision_layer_id.setText(collisionIDs.get(i) + "");
    			
    			// Add attributes to tile element
    			tempTile.addContent(tile_layer_id);
    			tempTile.addContent(object_layer_id);
    			tempTile.addContent(collision_layer_id);
    			
    			// Add tile element to map
    			map.addContent(tempTile);
    		}
             
            // Output the file
            FileWriter file;
			
			// Check to see if the .map extension doesn't already exist on the file
			if (!filePath.contains(".tmf"))
				// If it doesn't, add it
				file = new FileWriter(filePath + ".tmf");
			else
				// Otherwise, don't add it
				file = new FileWriter(filePath);
			
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, file);
        } 
            
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
}