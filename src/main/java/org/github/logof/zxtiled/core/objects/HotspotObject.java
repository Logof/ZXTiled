package org.github.logof.zxtiled.core.objects;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.enums.HotspotEnum;
import org.github.logof.zxtiled.mapeditor.enums.MapObjectGlobalTypeEnum;
import java.awt.*;

@Getter
@Setter
public class HotspotObject extends MapObject {

    private HotspotEnum type;

    public HotspotObject(int x,
                         int y,
                         int screenNumber) {
        super(MapObjectGlobalTypeEnum.HOTSPOT, x, y, screenNumber);
    }


    @Override
    public String toString() {
        return type + " (" + getX() + "," + getY() + ")";
    }

    @Override
    public void repaint(Graphics graphic, double zoom) {
        Graphics2D graphics2D = (Graphics2D) graphic;

        int coordinateX = (int) (getCoordinateXAt() * Constants.TILE_WIDTH * zoom);
        int coordinateY = (int) (getCoordinateYAt() * Constants.TILE_HEIGHT * zoom);

        Image objectImage = getImage(zoom);
        if (objectImage != null) {
            graphics2D.drawImage(objectImage, coordinateX, coordinateY, null);
        }

        graphics2D.setColor(Color.RED);
        graphics2D.drawRect(
                coordinateX,
                coordinateY,
                (int) (Constants.TILE_WIDTH * zoom),
                (int) (Constants.TILE_HEIGHT * zoom));
    }
}
