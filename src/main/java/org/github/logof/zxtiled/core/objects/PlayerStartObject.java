package org.github.logof.zxtiled.core.objects;

import org.github.logof.zxtiled.mapeditor.enums.MapObjectGlobalTypeEnum;
import java.awt.*;

public class PlayerStartObject extends MapObject {
    public PlayerStartObject(int x,
                             int y,
                             int screenNumber) {
        super(MapObjectGlobalTypeEnum.PLAYER_START, x, y, screenNumber);
    }

    @Override
    public String toString() {
        return "PLAYER_START (" + getX() + "," + getY() + ")";
    }

    @Override
    public void repaint(Graphics graphic, double zoom) {

    }
}
