package org.github.logof.zxtiled.mapeditor.listener;

import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.enums.PointerStateEnum;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapEditorActionListener implements ActionListener {

    private final MapEditor mapEditor;

    public MapEditorActionListener(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();

        if ("paint".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_PAINT);
            mapEditor.resetBrush();
        } else if ("erase".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_ERASE);
            mapEditor.resetBrush();
        } else if ("point".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_POINT);
            mapEditor.resetBrush();
        } else if ("pour".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_POUR);
            mapEditor.resetBrush();
        } else if ("eyed".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_EYED);
            mapEditor.resetBrush();
        } else if ("marquee".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_MARQUEE);
            mapEditor.resetBrush();
        } else if ("move".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_MOVE);
            mapEditor.resetBrush();
        } else if ("addobject".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_ADD_OBJ);
            mapEditor.resetBrush();
        } else if ("removeobject".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_REMOVE_OBJ);
            mapEditor.resetBrush();
        } else if ("moveobject".equals(command)) {
            mapEditor.getPointerStateManager().setCurrentPointerState(PointerStateEnum.PS_MOVE_OBJ);
            mapEditor.resetBrush();
        } else {
            mapEditor.handleEvent(actionEvent);
        }
    }

}

