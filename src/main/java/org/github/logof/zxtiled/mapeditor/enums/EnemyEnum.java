package org.github.logof.zxtiled.mapeditor.enums;

import org.github.logof.zxtiled.core.MapTypeEnum;

public enum EnemyEnum {
    ENEMY_1,
    ENEMY_2,
    ENEMY_3,
    ENEMY_4,
    MOVE_PLATFORM;


    public static EnemyEnum[] getValuesByMapType(MapTypeEnum mapTypeEnum) {
        if (MapTypeEnum.MAP_SIDE_SCROLLED == mapTypeEnum) {
            return new EnemyEnum[]{ENEMY_1, ENEMY_2, ENEMY_3, MOVE_PLATFORM};
        }
        return new EnemyEnum[]{ENEMY_1, ENEMY_2, ENEMY_3, ENEMY_4};
    }


}
