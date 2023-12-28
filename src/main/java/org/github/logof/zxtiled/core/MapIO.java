package org.github.logof.zxtiled.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.SneakyThrows;
import org.github.logof.zxtiled.generated.Project;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Handles loading and saving maps. Also handles Base64 encoding and decoding
 * for the tile sheet image.
 */
public class MapIO 
{
	private static Project PROJECT_FILE = new Project();

	/**
	 * Loads the .tmf file into the program and sets it up to be edited
	 * @param fileName - The file path to read the project from
	 * @param parentFrame - The MapperFrame in use
	 */
	@SneakyThrows
	public static void loadProjectAsXML(String fileName, MapperFrame parentFrame) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Project.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		PROJECT_FILE = (Project) unmarshaller.unmarshal(new File(fileName));

		int mapWidth = PROJECT_FILE.getMap().getWidth() * 16;
		int mapHeight = PROJECT_FILE.getMap().getHeight() * 10;

		// Get tile, object, and collision layer data

		Map<Integer, Integer> tilesLayerData = new HashMap<>();

		List<Byte> collisionLayerData = new ArrayList<>();

		// Iterate through each tile element
		PROJECT_FILE.getMap().getTile().forEach(tile ->
			tilesLayerData.put(tile.getId(), tile.getLayer())
											   );

		// Convert Base64 string to the image
		byte[] imageBytes = Base64.getDecoder().decode(PROJECT_FILE.getGraphic().getTiles().getBase64());
		InputStream in = new ByteArrayInputStream(imageBytes);
		BufferedImage decodedImage = ImageIO.read(in);

		// Create the tile sheet from the variables that have been read
		TileSheet sheet = new TileSheet(decodedImage);

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

		createLayoutManager(parentFrame, mapPanel);

		// Set tiles and object data to the MapPanel
		mapPanel.setLayerData(tilesLayerData, collisionLayerData);
	}

	private static void createLayoutManager(MapperFrame parentFrame, MapPanel mapPanel) {
		LayoutManager manager;
		if (parentFrame.getLayoutManager() == null) {
			manager = new LayoutManager(parentFrame, mapPanel);
			parentFrame.setLayoutManager(manager);
		} else {
			manager = parentFrame.getLayoutManager();
			manager.clearExistingLayout();
			manager.setNewInfo(parentFrame, mapPanel);
		}
		manager.initializeLayout();
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