package org.github.logof.zxtiled.util;

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
        int posX = mouseX / (int) (320 * zoom);
        int posY = mouseY / (int) (160 * zoom);
        return posX + (posY * screenWidth);
    }
}
