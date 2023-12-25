package org.github.logof.zxtiled.enums;

import lombok.AllArgsConstructor;
import java.util.Arrays;

@AllArgsConstructor
public enum TileTypeEnum {
    PRIMARY_BACKGROUND(0, 0),
    BACKGROUND(1, 13),
    PUSH(14, 14),
    LOCK(15, 15),
    LIFE(16, 16),
    COLLECTABLE(17, 17),
    KEY(18, 18),
    ALTERNATIVE_BACKGROUND(19, 19),
    SHADOW(20,20);

    private final int minTypeId;
    private final int maxTypeId;

    public static TileTypeEnum getTileTypeById(int id) {
        return Arrays.stream(TileTypeEnum.values())
                     .filter(r -> r.minTypeId >= id || r.maxTypeId <= id)
                     .findFirst().orElseThrow();
    }
}
