package org.github.logof.zxtiled.mapeditor;

import java.awt.*;

public class Constants {
    public static final String DTD = "/home/user/mojontwins/ZXTiled/map.dtd";
    public static final int SCREEN_WIDTH = 15;
    public static final int SCREEN_HEIGHT = 10;

    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;

    public static final Cursor CURSOR_EYED = new Cursor(Cursor.CROSSHAIR_CURSOR);
    public static final Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);

    public static final String IMPORT_ERROR_MSG = Resources.getString("dialog.newtileset.import.error.message");
    public static final String PANEL_TILE_PALETTE = Resources.getString("panel.tilepalette.title");
    public static final String PANEL_LAYERS = Resources.getString("panel.layers.title");
    public static final String TOOL_PAINT = Resources.getString("tool.paint.name");
    public static final String TOOL_ERASE = Resources.getString("tool.erase.name");
    public static final String TOOL_FILL = Resources.getString("tool.fill.name");
    public static final String TOOL_EYE_DROPPER = Resources.getString("tool.eyedropper.name");
    public static final String TOOL_SELECT = Resources.getString("tool.select.name");
    public static final String TOOL_MOVE_LAYER = Resources.getString("tool.movelayer.name");
    public static final String TOOL_ADD_OBJECT = Resources.getString("tool.addobject.name");
    public static final String TOOL_REMOVE_OBJECT = Resources.getString("tool.removeobject.name");
    public static final String TOOL_MOVE_OBJECT = Resources.getString("tool.moveobject.name");
    public static final String STATUS_PAINT_ERROR_LAYER_LOCKED = Resources.getString("status.paint.error.layer.locked");
    public static final String STATUS_PAINT_ERROR_LAYER_INVISIBLE = Resources.getString("status.paint.error.layer.invisible");
    public static final String STATUS_PAINT_ERROR_GENERAL = Resources.getString("status.paint.error.general");
    public static final String STATUS_FILE_INFO_LOAD_SUCCESS = Resources.getString("status.file.info.load.success");
    public static final String STATUS_FILE_ERROR_LOAD_FAILURE = Resources.getString("status.file.error.load.failure");
    public static final String STATUS_LAYER_SELECTED_FORMAT = Resources.getString("status.layer.selectedformat_name_w_h_x_y_tilew_tileh");
    public static final String STATUS_LAYER_MOVED_FORMAT = Resources.getString("status.layer.movedformat_x_y");
}
