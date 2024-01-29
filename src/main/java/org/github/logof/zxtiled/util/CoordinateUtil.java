package org.github.logof.zxtiled.util;

import static org.github.logof.zxtiled.mapeditor.Constants.SCREEN_HEIGHT;
import static org.github.logof.zxtiled.mapeditor.Constants.SCREEN_WIDTH;
import static org.github.logof.zxtiled.mapeditor.Constants.TILE_HEIGHT;
import static org.github.logof.zxtiled.mapeditor.Constants.TILE_WIDTH;

public class CoordinateUtil {

    public static void mouseToTile(int x, int y) {
        int posX = x / 16;
        int posY = y / 16;
    }

    /**
     * Screen 16*15 = 320
     * 16*10 = 160
     *
     * @param mouseX mouse position by X
     * @param mouseY mouse position by Y
     */
    public static int mouseToScreenNumber(int mouseX, int mouseY, int screenWidth, float zoom) {
        int posX = mouseX / (int) (SCREEN_WIDTH * TILE_WIDTH * zoom);
        int posY = mouseY / (int) (SCREEN_HEIGHT * TILE_HEIGHT * zoom);
        return posX + (posY * screenWidth);
    }
}
