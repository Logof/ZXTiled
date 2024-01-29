package org.github.logof.zxtiled.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MapTypeEnum {
    MAP_SIDE_SCROLLED("side_scrolled"),
    MAP_TOP_DOWN("top_down");


    private final String name;
}
