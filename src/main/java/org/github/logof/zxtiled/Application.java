package org.github.logof.zxtiled;

import org.github.logof.zxtiled.core.MapperFrame;

/**
 * Application class. Runs the MapperFrame
 */
public class Application
{
	public static void main(String[] args)
	{
		MapperFrame frame = new MapperFrame();
		frame.setVisible(true);
		frame.showSplash();
	}	
}

