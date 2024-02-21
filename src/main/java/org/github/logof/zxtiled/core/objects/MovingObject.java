package org.github.logof.zxtiled.core.objects;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.enums.MapObjectGlobalTypeEnum;
import org.github.logof.zxtiled.mapeditor.enums.MovingObjectTypeEnum;
import org.github.logof.zxtiled.mapeditor.gui.graphics.LineArrow;
import java.awt.*;
import java.util.Objects;

@Getter
@Setter
public class MovingObject extends MapObject {

    private int objectSpeed;

    private Point finalPoint;

    private MovingObjectTypeEnum type;

    public MovingObject(int x,
                        int y,
                        int screenNumber) {
        super(MapObjectGlobalTypeEnum.ENEMY, x, y, screenNumber);

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

        graphics2D.setColor(Color.ORANGE);
        graphics2D.drawRect(
                coordinateX,
                coordinateY,
                (int) (Constants.TILE_WIDTH * zoom),
                (int) (Constants.TILE_HEIGHT * zoom));

        int finalCoordinatesXAt = Objects.nonNull(getFinalPoint()) ? getFinalPoint().x : getCoordinateXAt();
        int finalCoordinatesYAt = Objects.nonNull(getFinalPoint()) ? getFinalPoint().y : getCoordinateYAt();
        int coordinateStartLineX = coordinateX;
        int coordinateStartLineY = coordinateY;
        int coordinateFinalLineX = (int) (finalCoordinatesXAt * Constants.TILE_WIDTH * zoom);
        int coordinateFinalLineY = (int) (finalCoordinatesYAt * Constants.TILE_HEIGHT * zoom);

        // При рисовании может быть 3 случая
        // 1. Начало и конец лежат на одной оси Х (меняется Y)
        if (finalCoordinatesXAt == getCoordinateXAt() && finalCoordinatesYAt != getCoordinateYAt()) {
            coordinateStartLineX = coordinateStartLineX + (int) (Constants.TILE_WIDTH * zoom / 2);
            coordinateStartLineY = coordinateStartLineY + (int) (Constants.TILE_WIDTH * zoom);
            coordinateFinalLineX = coordinateFinalLineX + (int) (Constants.TILE_WIDTH * zoom / 2);
        }

        // 2. Начало и конец лежат на одной оси Y (меняется X)
        if (finalCoordinatesXAt != getCoordinateXAt() && finalCoordinatesYAt == getCoordinateYAt()) {
            coordinateStartLineX = coordinateStartLineX + (int) (Constants.TILE_WIDTH * zoom);
            coordinateStartLineY = coordinateStartLineY + (int) (Constants.TILE_WIDTH * zoom / 2);
            coordinateFinalLineY = coordinateFinalLineY + (int) (Constants.TILE_WIDTH * zoom / 2);
        }
        // 3. Начало и конец совпадают ни на одной оси (меняются X и Y)
        if (finalCoordinatesXAt != getCoordinateXAt() && finalCoordinatesYAt != getCoordinateYAt()) {
            if (finalCoordinatesXAt > getCoordinateXAt()) {
                coordinateStartLineX = coordinateStartLineX + (int) (Constants.TILE_WIDTH * zoom);
            } else {
                coordinateFinalLineX = coordinateFinalLineX + (int) (Constants.TILE_WIDTH * zoom);
            }

            if (finalCoordinatesYAt > getCoordinateYAt()) {
                coordinateStartLineY = coordinateStartLineY + (int) (Constants.TILE_WIDTH * zoom);
            } else {
                coordinateFinalLineY = coordinateFinalLineY + (int) (Constants.TILE_WIDTH * zoom);
            }
        }

        if (Objects.nonNull(getFinalPoint()) && (getCoordinateXAt() != getFinalPoint().x || getCoordinateYAt() != getFinalPoint().y)) {
            // Рисуем путь объекта
            LineArrow lineArrow = new LineArrow(coordinateStartLineX, coordinateStartLineY, coordinateFinalLineX, coordinateFinalLineY,
                    Color.BLUE, (int) (zoom));
            lineArrow.draw(graphics2D);

            // Рисуем квадрат назначения
            graphics2D.setColor(Color.BLUE);
            graphics2D.drawRect(
                    finalCoordinatesXAt * (int) (Constants.TILE_WIDTH * zoom),
                    finalCoordinatesYAt * (int) (Constants.TILE_HEIGHT * zoom),
                    (int) (Constants.TILE_WIDTH * zoom),
                    (int) (Constants.TILE_HEIGHT * zoom));
        }

        // Имя
        final String s = getName() != null ? getName() : "(null)";
        graphics2D.setColor(Color.black);
        graphics2D.drawString(s, (coordinateX - 5) + 1, (coordinateY - 5) + 1);
        graphics2D.setColor(Color.white);
        graphics2D.drawString(s, (coordinateX - 5), (coordinateY - 5));
    }

}
