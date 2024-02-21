package org.github.logof.zxtiled.mapeditor.enums;

import org.github.logof.zxtiled.core.MapTypeEnum;

public enum MovingObjectTypeEnum implements MapObjectTypeEnum {
    ENEMY_1,
    ENEMY_2,
    ENEMY_3,
    ENEMY_4,
    MOVE_PLATFORM;


    public static MovingObjectTypeEnum[] getValuesByMapType(MapTypeEnum mapTypeEnum) {
        if (MapTypeEnum.MAP_SIDE_SCROLLED == mapTypeEnum) {
            return new MovingObjectTypeEnum[]{ENEMY_1, ENEMY_2, ENEMY_3, MOVE_PLATFORM};
        }
        return new MovingObjectTypeEnum[]{ENEMY_1, ENEMY_2, ENEMY_3, ENEMY_4};
    }


}
