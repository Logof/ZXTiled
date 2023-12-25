package org.github.logof.zxtiled.core;
import lombok.Getter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Icon;

/**
 * A tile which can be selected in one of the TilePanels.
 */
@Getter
public class Tile extends JLabel
{
	private boolean selected = false;
	private final Image image;
	private final Icon iconImage;
	private final TilePanel parentTilePanel;
	private final int id;
	
	/**
	 * This constructs a drawable tile to be displayed on a TilePanel
 	 * which can be added to a TilePanel
	 * @param eraseTile - The image to be drawn
	 * @param tilePanel - The TilePanel to which this instance of Tile belongs
	 * @param id - A unique ID, used for the selection functionality
	 */
	public Tile(Image eraseTile, TilePanel tilePanel, int id)
	{
		super(new ImageIcon(eraseTile));
		iconImage = new ImageIcon(eraseTile);
		this.id = id;
		this.image = eraseTile;
		parentTilePanel = tilePanel;
		addMouseListener(new TileListener());
	}

	/**
	 * @Override of the JLabel method
	 * Paints the component. If selected,
	 * paints a box around the tile
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (selected)
		{
			// Draw the selection square 
			Graphics2D g2 = (Graphics2D) g;
			g.setColor(Color.CYAN);
			g2.setStroke(new BasicStroke(3.5f));
			g2.drawRect(0, 0, iconImage.getIconWidth(), iconImage.getIconHeight());
		}
	}
	
	/**
	 * Sets the selection status of this Tile
	 * @param flag - Whether or not this is the currently selected Tile
	 */
	public void setSelected(boolean flag)
	{
		selected = flag;
		repaint();
	}

	/**
	 * Simple MouseListener used for updating the selected Tile
	 */
	class TileListener implements MouseListener
	{
		public void mousePressed(MouseEvent e)
		{
			// Tell the MapPanel which panel was selected last
			parentTilePanel.getMapPanel().setObjectPanelSelectedLast(parentTilePanel.isObjectPanel());

			// Tell the parent TilePanel that this tile has been selected
            parentTilePanel.setSelectedTile(Math.max(id, 0));
			repaint();
		}
		
		public void mouseEntered(MouseEvent e)
		{
			if (e.getModifiers() == 16)
			{
				// Tell the MapPanel which panel was selected last
				parentTilePanel.getMapPanel().setObjectPanelSelectedLast(parentTilePanel.isObjectPanel());

				// Tell the parent TilePanel that this tile has been selected
                parentTilePanel.setSelectedTile(Math.max(id, 0));
				repaint();
			}
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
}