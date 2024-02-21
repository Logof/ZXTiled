package org.github.logof.zxtiled.mapeditor.listener;

import org.github.logof.zxtiled.core.PointerStateManager;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.enums.PointerStateEnum;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapEditorActionListener implements ActionListener {

    private final MapEditor mapEditor;

    private final PointerStateManager pointerStateManager;


    public MapEditorActionListener(MapEditor mapEditor, PointerStateManager pointerStateManager) {
        this.mapEditor = mapEditor;
        this.pointerStateManager = pointerStateManager;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();

        if ("paint".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_PAINT);
        } else if ("erase".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_ERASE);
        } else if ("point".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_POINT);
        } else if ("pour".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_POUR);
        } else if ("marquee".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_MARQUEE);
        } else if ("move".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_MOVE);
        } else if ("addobject".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_ADD_OBJ);
        } else if ("addhotspot".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_ADD_HOTSPOT);
        } else if ("removeobject".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_REMOVE_OBJ);
        } else if ("moveobject".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_MOVE_OBJ);
        } else if ("startPointObject".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_START_OBJECT);
        } else if ("finishPointButton".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_FINISH_OBJECT);
        } else {
            mapEditor.handleEvent(actionEvent);
        }
    }

}

