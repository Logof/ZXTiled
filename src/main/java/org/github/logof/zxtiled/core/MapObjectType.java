package org.github.logof.zxtiled.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum MapObjectType {
    ENEMY_TYPE_01(1, "Враги с горизонтальной/вертикальной троекторией"),
    ENEMY_TYPE_02(2, "Движение по диагонали"),
    ENEMY_TYPE_03(3, "???"),
    PLATFORM_TYPE_01(4, "Движущаяся платформа (горизонталь / вертикаль)"),
    ENEMY_TYPE_05(5, "???"),
    ENEMY_TYPE_06(6, "Летающие враги, которые следуют за ГГ (могут преследовать на другом экране)"),
    ENEMY_TYPE_07(7, "Постоянно появляющиеся враги, атакующие ГГ"),

    // Предметы
    HOTSPOT_TYPE_01(1, "Коллекционный предмет"),
    HOTSPOT_TYPE_02(2, "Ключ"),
    HOTSPOT_TYPE_03(3, "Жизнь"),
    HOTSPOT_TYPE_04(4, "Боеприпасы"),
    HOTSPOT_TYPE_05(5, "Время");

    private final int id;
    private final String descryption;

    public static MapObjectType getById(int id) {
        return Arrays.stream(MapObjectType.values())
                     .filter(e -> Objects.equals(e.id, id)).findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             String.format("Не удалось определить ECertificateStatus по id %s", id)));
    }

    public static Vector<MapObjectType> getAllValues() {
        return Arrays.stream(MapObjectType.values()).collect(Collectors.toCollection(Vector::new));
    }
}
