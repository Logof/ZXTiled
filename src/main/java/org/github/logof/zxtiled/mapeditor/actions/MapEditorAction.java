package org.github.logof.zxtiled.mapeditor.actions;

import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.mapeditor.MapEditor;
import javax.swing.*;

public class MapEditorAction {
    public static SaveAction saveAction;
    public static SaveAsAction saveAsAction;
    public static SaveAsImageAction saveAsImageAction;
    public static ExportAction exportAction;
    public static Action exitAction;
    public static Action zoomInAction;
    public static Action zoomOutAction;
    public static Action zoomNormalAction;
    public static Action flipHorAction;
    public static Action flipVerAction;
    public static Action selectAllAction;
    public static Action inverseAction;
    public static Action cancelSelectionAction;
    public static Action addLayerAction;
    public static Action cloneLayerAction;
    public static Action deleteLayerAction;
    public static Action moveLayerDownAction;
    public static Action moveLayerUpAction;
    public static Action mergeLayerDownAction;
    public static Action mergeAllLayersAction;
    public static Action addObjectGroupAction;
    public static Action showLayerPropertiesAction;

    public static NewMapAction newMapAction;
    public static OpenMapAction openMapAction;
    public static CloseMapAction closeMapAction;

    public static CopyAction copyAction;
    public static CopyAllAction copyAllAction;
    public static CutAction cutAction;
    public static PasteAction pasteAction;

    public static void init(MapEditor mapEditor) {
        saveAction = new SaveAction(mapEditor);
        saveAsAction = new SaveAsAction(mapEditor);
        saveAsImageAction = new SaveAsImageAction(mapEditor);
        exportAction = new ExportAction(mapEditor);
        exitAction = new ExitAction(mapEditor, saveAction);
        zoomInAction = new ZoomInAction(mapEditor);
        zoomOutAction = new ZoomOutAction(mapEditor);
        zoomNormalAction = new ZoomNormalAction(mapEditor);
        flipHorAction = new LayerTransformAction(mapEditor, MapLayer.MIRROR_HORIZONTAL);
        flipVerAction = new LayerTransformAction(mapEditor, MapLayer.MIRROR_HORIZONTAL);
        selectAllAction = new SelectAllAction(mapEditor);
        cancelSelectionAction = new CancelSelectionAction(mapEditor);
        inverseAction = new InverseSelectionAction(mapEditor);
        addLayerAction = new AddLayerAction(mapEditor);
        cloneLayerAction = new CloneLayerAction(mapEditor);
        deleteLayerAction = new DeleteLayerAction(mapEditor);
        moveLayerUpAction = new MoveLayerUpAction(mapEditor);
        moveLayerDownAction = new MoveLayerDownAction(mapEditor);
        mergeLayerDownAction = new MergeLayerDownAction(mapEditor);
        mergeAllLayersAction = new MergeAllLayersAction(mapEditor);
        addObjectGroupAction = new AddObjectGroupAction(mapEditor);
        showLayerPropertiesAction = new ShowLayerPropertiesAction(mapEditor);

        // File menu
        newMapAction = new NewMapAction(mapEditor, saveAction);
        openMapAction = new OpenMapAction(mapEditor, saveAction);
        closeMapAction = new CloseMapAction(mapEditor, saveAction);

        // Edit menu
        copyAction = new CopyAction(mapEditor);
        copyAllAction = new CopyAllAction(mapEditor);
        cutAction = new CutAction(mapEditor);
        pasteAction = new PasteAction(mapEditor);
    }
}
