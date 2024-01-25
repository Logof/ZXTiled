package org.github.logof.prototype;

// Слой тайлов на карте
// 1.
public class TileLayer implements MapLayer {
    private Tile[][] layerData;
    private final int widthScreen;
    private final int heightScreen;
    private final int width;
    private final int height;

    public TileLayer(int widthScreen, int heightScreen) {
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;
        width = widthScreen * 15;
        height = heightScreen * 10;

        layerData = new Tile[width][height];
    }
}
