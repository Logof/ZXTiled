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
            mapEditor.resetBrush();
        } else if ("erase".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_ERASE);
            mapEditor.resetBrush();
        } else if ("point".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_POINT);
            mapEditor.resetBrush();
        } else if ("pour".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_POUR);
            mapEditor.resetBrush();
        } else if ("marquee".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_MARQUEE);
            mapEditor.resetBrush();
        } else if ("move".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_MOVE);
            mapEditor.resetBrush();
        } else if ("addobject".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_ADD_OBJ);
            mapEditor.resetBrush();
        } else if ("removeobject".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_REMOVE_OBJ);
            mapEditor.resetBrush();
        } else if ("moveobject".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_MOVE_OBJ);
            mapEditor.resetBrush();
        } else if ("startPointObject".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_START_OBJECT);
            mapEditor.resetBrush();
        } else if ("finishPointButton".equals(command)) {
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_FINISH_OBJECT);
            mapEditor.resetBrush();
        } else {
            mapEditor.handleEvent(actionEvent);
        }
    }

}

