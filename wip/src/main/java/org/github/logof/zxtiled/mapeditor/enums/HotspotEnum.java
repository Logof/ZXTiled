package org.github.logof.zxtiled.mapeditor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.github.logof.zxtiled.core.MapTypeEnum;
import org.github.logof.zxtiled.mapeditor.Resources;

@Getter
@AllArgsConstructor
public enum HotspotEnum implements MapObjectTypeEnum {
    ITEM_COLLECTABLE(0, Resources.getString("hotspot.collectable.name")),
    ITEM_KEY(1, Resources.getString("hotspot.key.name")),
    ITEM_LIVE(2, Resources.getString("hotspot.live.name")),
    ITEM_ARMOR(3, Resources.getString("hotspot.armor.name")),
    ITEM_TIME(4, Resources.getString("hotspot.time.name"));

    private final Integer id;
    private final String value;

    public static HotspotEnum[] getValuesByMapType(MapTypeEnum mapTypeEnum) {
        return new HotspotEnum[]{ITEM_COLLECTABLE, ITEM_KEY, ITEM_LIVE, ITEM_ARMOR, ITEM_TIME};
    }
}
