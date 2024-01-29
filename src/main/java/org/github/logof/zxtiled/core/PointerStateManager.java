package org.github.logof.zxtiled.core;

import lombok.Getter;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.enums.PointerStateEnum;
import org.github.logof.zxtiled.mapeditor.selection.ToolSemantic;

public class PointerStateManager {
    @Getter
    private static PointerStateEnum currentPointerState;

    private static ToolSemantic currentToolSemantic;

    private final MapEditor mapEditor;

    public PointerStateManager(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    public void setCurrentPointerState(PointerStateEnum state) {
        currentPointerState = state;

        mapEditor.getToolBar().setCurrentPointerState(state);

        // Set the matching cursor
        if (mapEditor.getMapView() != null) {
            switch (currentPointerState) {
                case PS_PAINT:
                case PS_ERASE:
                case PS_POINT:
                case PS_POUR:
                case PS_MARQUEE:
                case PS_START_OBJECT:
                case PS_FINISH_OBJECT:
                    mapEditor.getMapView().setCursor(Constants.CURSOR_DEFAULT);
                    break;
                case PS_EYED:
                    mapEditor.getMapView().setCursor(Constants.CURSOR_EYED);
                    break;
            }
        }
        updateToolSemantics();
    }

    public void updateToolSemantics() {
        // FIXME: this is currently very simple, but should be replaced
        //  by something that is more powerful - when the tools are refactored
        //  and moved out of MapEditor altogether..
        ToolSemantic toolSemantic;
        if (currentPointerState == PointerStateEnum.PS_MARQUEE && ObjectLayer.class.isAssignableFrom(mapEditor.getCurrentLayer()
                                                                                                              .getClass())) {
            toolSemantic = mapEditor.getObjectSelectionToolSemantic();
        } else {
            toolSemantic = null;
        }

        if (toolSemantic == currentToolSemantic) {
            return;
        }
        if (currentToolSemantic != null) {
            currentToolSemantic.deactivate();
        }

        currentToolSemantic = toolSemantic;

        if (currentToolSemantic != null) {
            currentToolSemantic.activate();
        }
    }

}
