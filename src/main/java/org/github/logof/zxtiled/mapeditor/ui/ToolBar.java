package org.github.logof.zxtiled.mapeditor.ui;

import lombok.Getter;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.enums.PointerStateEnum;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import javax.swing.*;
import java.awt.*;

public class ToolBar extends JToolBar {
    private static final Icon iconMove = Resources.getIcon("icon/gimp-tool-move-22.png");
    private static final Icon iconPaint = Resources.getIcon("icon/gimp-tool-pencil-22.png");
    private static final Icon iconErase = Resources.getIcon("icon/gimp-tool-eraser-22.png");
    private static final Icon iconPour = Resources.getIcon("icon/gimp-tool-bucket-fill-22.png");
    private static final Icon iconEyed = Resources.getIcon("icon/gimp-tool-color-picker-22.png");
    private static final Icon iconMarquee = Resources.getIcon("icon/gimp-tool-rect-select-22.png");
    private static final Icon iconAddObject = Resources.getIcon("icon/gnome-list-add-22.png");
    private static final Icon iconRemoveObject = Resources.getIcon("icon/gnome-list-remove-22.png");
    private static final Icon iconMoveObject = Resources.getIcon("icon/gimp-tool-object-move-22.png");

    private AbstractButton paintButton;
    private AbstractButton eraseButton;
    private AbstractButton pourButton;
    private AbstractButton eyedButton;
    private AbstractButton marqueeButton;
    private AbstractButton moveButton;
    private AbstractButton objectMoveButton;
    private AbstractButton objectAddButton;
    private AbstractButton objectRemoveButton;

    @Getter
    private static final BrushPreview brushPreview = new BrushPreview();

    public ToolBar() {
        super(JToolBar.VERTICAL);

        paintButton = createToggleButton(iconPaint, "paint", Constants.TOOL_PAINT);
        eraseButton = createToggleButton(iconErase, "erase", Constants.TOOL_ERASE);
        pourButton = createToggleButton(iconPour, "pour", Constants.TOOL_FILL);
        eyedButton = createToggleButton(iconEyed, "eyed", Constants.TOOL_EYE_DROPPER);
        marqueeButton = createToggleButton(iconMarquee, "marquee", Constants.TOOL_SELECT);
        moveButton = createToggleButton(iconMove, "move", Constants.TOOL_MOVE_LAYER);
        objectAddButton = createToggleButton(iconAddObject, "addobject", Constants.TOOL_ADD_OBJECT);
        objectRemoveButton = createToggleButton(iconRemoveObject, "removeobject", Constants.TOOL_REMOVE_OBJECT);
        objectMoveButton = createToggleButton(iconMoveObject, "moveobject", Constants.TOOL_MOVE_OBJECT);

        MapEventAdapter.addListener(moveButton);
        MapEventAdapter.addListener(paintButton);
        MapEventAdapter.addListener(eraseButton);
        MapEventAdapter.addListener(pourButton);
        MapEventAdapter.addListener(eyedButton);
        MapEventAdapter.addListener(marqueeButton);
        MapEventAdapter.addListener(objectMoveButton);

        this.setFloatable(true);
        this.add(moveButton);
        this.add(paintButton);
        this.add(eraseButton);
        this.add(pourButton);
        this.add(eyedButton);
        this.add(marqueeButton);
        this.add(Box.createRigidArea(new Dimension(5, 5)));
        this.add(objectAddButton);
        this.add(objectRemoveButton);
        this.add(objectMoveButton);
        this.add(Box.createRigidArea(new Dimension(0, 5)));
        this.add(new TButton(MapEditorAction.zoomInAction));
        this.add(new TButton(MapEditorAction.zoomOutAction));
        this.add(Box.createRigidArea(new Dimension(5, 5)));
        this.add(Box.createGlue());

        MapEventAdapter.addListener(brushPreview);
        this.add(brushPreview);

    }

    private AbstractButton createToggleButton(Icon icon, String command, String tipText) {
        AbstractButton button;
        button = new JToggleButton("", icon);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setActionCommand(command);
        button.addActionListener(MapEditor.getActionListener());
        if (tipText != null) {
            button.setToolTipText(tipText);
        }
        return button;
    }

    public void setCurrentPointerState(PointerStateEnum state) {
        paintButton.setSelected(state == PointerStateEnum.PS_PAINT);
        eraseButton.setSelected(state == PointerStateEnum.PS_ERASE);
        pourButton.setSelected(state == PointerStateEnum.PS_POUR);
        eyedButton.setSelected(state == PointerStateEnum.PS_EYED);
        marqueeButton.setSelected(state == PointerStateEnum.PS_MARQUEE);
        moveButton.setSelected(state == PointerStateEnum.PS_MOVE);
        objectAddButton.setSelected(state == PointerStateEnum.PS_ADD_OBJ);
        objectRemoveButton.setSelected(state == PointerStateEnum.PS_REMOVE_OBJ);
        objectMoveButton.setSelected(state == PointerStateEnum.PS_MOVE_OBJ);
    }

    public void updateTileLayerOperations(boolean isEnable) {
        paintButton.setEnabled(isEnable);
        eraseButton.setEnabled(isEnable);
        pourButton.setEnabled(isEnable);
        eyedButton.setEnabled(isEnable);
    }

    public void updateValidSelectionOperations(boolean isEnable) {
        moveButton.setEnabled(isEnable);
    }

    public void updateObjectGroupOperations(boolean isEnable) {
        objectAddButton.setEnabled(isEnable);
        objectRemoveButton.setEnabled(isEnable);
        objectMoveButton.setEnabled(isEnable);
    }
}
