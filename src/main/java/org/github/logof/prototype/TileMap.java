package org.github.logof.prototype;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileMap {
    private final int widthScreen;
    private final int heightScreen;
    private final int width;
    private final int height;
    private final MapLayer objectLayer;
    private final MapLayer tileLayer;
    private final MapLayer activeMapLayer;
    private final Tileset tileset;

    public TileMap(int widthScreen, int heightScreen, Tileset tileset) {
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;

        width = widthScreen * 15;
        height = heightScreen * 10;
        this.tileset = tileset;

        this.tileLayer = new TileLayer(widthScreen, heightScreen);
        this.objectLayer = new ObjectLayer();
        this.activeMapLayer = tileLayer;
    }
}
