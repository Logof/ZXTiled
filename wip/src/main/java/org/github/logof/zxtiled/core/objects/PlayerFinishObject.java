package org.github.logof.zxtiled.core.objects;

import org.github.logof.zxtiled.mapeditor.enums.MapObjectGlobalTypeEnum;
import java.awt.*;

public class PlayerFinishObject extends MapObject {

    public PlayerFinishObject(int x,
                              int y,
                              int screenNumber) {
        super(x, y, screenNumber);
    }

    @Override
    public String toString() {
        return "PLAYER_FINISH (" + getX() + "," + getY() + ")";
    }

    @Override
    public void repaint(Graphics graphic, double zoom) {

    }
}
