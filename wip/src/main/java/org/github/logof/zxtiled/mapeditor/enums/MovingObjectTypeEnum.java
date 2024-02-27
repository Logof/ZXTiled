package org.github.logof.zxtiled.mapeditor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.github.logof.zxtiled.core.MapTypeEnum;

@AllArgsConstructor
@Getter
public enum MovingObjectTypeEnum implements MapObjectTypeEnum {
    ENEMY_1(1),
    ENEMY_2(2),
    ENEMY_3(3),
    ENEMY_4(4),
    MOVE_PLATFORM(5);

    private final int objectId;

    public static MovingObjectTypeEnum[] getValuesByMapType(MapTypeEnum mapTypeEnum) {
        if (MapTypeEnum.MAP_SIDE_SCROLLED == mapTypeEnum) {
            return new MovingObjectTypeEnum[]{ENEMY_1, ENEMY_2, ENEMY_3, MOVE_PLATFORM};
        }
        return new MovingObjectTypeEnum[]{ENEMY_1, ENEMY_2, ENEMY_3, ENEMY_4};
    }


}
