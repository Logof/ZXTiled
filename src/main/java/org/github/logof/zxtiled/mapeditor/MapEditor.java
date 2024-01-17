/*
 *  Tiled Map Editor, (c) 2004-2008
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package org.github.logof.zxtiled.mapeditor;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.zxtiled.core.MapChangeListener;
import org.github.logof.zxtiled.core.MapLayer;
import org.github.logof.zxtiled.core.MapObject;
import org.github.logof.zxtiled.core.ObjectGroup;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.TileSet;
import org.github.logof.zxtiled.core.event.MapChangedEvent;
import org.github.logof.zxtiled.core.event.MapLayerChangeEvent;
import org.github.logof.zxtiled.io.MapHelper;
import org.github.logof.zxtiled.io.MapReader;
import org.github.logof.zxtiled.mapeditor.actions.CloseMapAction;
import org.github.logof.zxtiled.mapeditor.actions.CopyAction;
import org.github.logof.zxtiled.mapeditor.actions.CopyAllAction;
import org.github.logof.zxtiled.mapeditor.actions.CutAction;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.actions.NewMapAction;
import org.github.logof.zxtiled.mapeditor.actions.OpenMapAction;
import org.github.logof.zxtiled.mapeditor.actions.OpenRecentAction;
import org.github.logof.zxtiled.mapeditor.actions.PasteAction;
import org.github.logof.zxtiled.mapeditor.brush.AbstractBrush;
import org.github.logof.zxtiled.mapeditor.brush.CustomBrush;
import org.github.logof.zxtiled.mapeditor.brush.ShapeBrush;
import org.github.logof.zxtiled.mapeditor.dialogs.AboutDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.BrushDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.ConfigurationDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.MapPropertiesDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.NewTilesetDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.PluginDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.PropertiesDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.ResizeDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.SearchDialog;
import org.github.logof.zxtiled.mapeditor.dialogs.TilesetManager;
import org.github.logof.zxtiled.mapeditor.enums.ActionModesEnum;
import org.github.logof.zxtiled.mapeditor.listener.MapMouseListener;
import org.github.logof.zxtiled.mapeditor.plugin.PluginClassLoader;
import org.github.logof.zxtiled.mapeditor.selection.ObjectSelectionToolSemantic;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import org.github.logof.zxtiled.mapeditor.selection.SelectionSet;
import org.github.logof.zxtiled.mapeditor.selection.ToolSemantic;
import org.github.logof.zxtiled.mapeditor.ui.ApplicationFrame;
import org.github.logof.zxtiled.mapeditor.ui.BrushPreview;
import org.github.logof.zxtiled.mapeditor.ui.FloatablePanel;
import org.github.logof.zxtiled.mapeditor.ui.MiniMapViewer;
import org.github.logof.zxtiled.mapeditor.ui.SmartSplitPane;
import org.github.logof.zxtiled.mapeditor.ui.TButton;
import org.github.logof.zxtiled.mapeditor.ui.TMenuItem;
import org.github.logof.zxtiled.mapeditor.ui.TabbedTilesetsPane;
import org.github.logof.zxtiled.mapeditor.ui.TilePalettePanel;
import org.github.logof.zxtiled.mapeditor.ui.TimedStatusLabel;
import org.github.logof.zxtiled.mapeditor.ui.menu.FileMenu;
import org.github.logof.zxtiled.mapeditor.ui.menu.MainMenuBar;
import org.github.logof.zxtiled.mapeditor.undo.MapLayerEdit;
import org.github.logof.zxtiled.mapeditor.undo.UndoHandler;
import org.github.logof.zxtiled.mapeditor.util.LayerTableModel;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import org.github.logof.zxtiled.mapeditor.util.TiledFileFilter;
import org.github.logof.zxtiled.util.TiledConfiguration;
import org.github.logof.zxtiled.view.MapView;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Vector;
import java.util.prefs.Preferences;

import static org.github.logof.zxtiled.view.MapView.ZOOM_NORMAL_SIZE;

/**
 * The main class for the Tiled Map Editor.
 */
public class MapEditor implements ActionListener,
                                  /*MouseListener,
                                  MouseMotionListener,
                                  MouseWheelListener,*/
                                  MapChangeListener,
                                  ListSelectionListener,
                                  ChangeListener,
                                  ComponentListener {
    // Constants and the like

    /**
     * Current release version.
     */
    public static final String version = "0.0.1";
    public static final Preferences preferences = TiledConfiguration.root();
    private final Cursor curDefault;
    private final Cursor curEyed;
    @Getter
    private final UndoHandler undoHandler;
    @Getter
    private final UndoableEditSupport undoSupport;
    @Getter
    private final PluginClassLoader pluginLoader;
    @Getter
    private final SelectionLayer cursorHighlight;
    @Getter
    private final ApplicationFrame appFrame;
    private final ObjectSelectionToolSemantic objectSelectionToolSemantic;
    @Getter
    private final SelectionSet selectionSet = new SelectionSet();
    @Getter
    private TileMap currentTileMap;
    @Getter
    private MapView mapView;
    @Getter
    private ActionModesEnum currentPointerState;
    @Getter
    private Tile currentTile;
    @Getter
    @Setter
    private MapObject currentObject = null;
    private int currentLayer = -1;

    @Getter
    private AbstractBrush currentBrush;
    @Getter
    @Setter
    private SelectionLayer marqueeSelection;
    @Getter
    @Setter
    private MapLayer clipboardLayer;
    private float relativeMidX, relativeMidY;
    private JPanel dataPanel;
    private JPanel statusBar;
    private MainMenuBar mainMenuBar;
    private JCheckBoxMenuItem gridMenuItem;
    private JCheckBoxMenuItem cursorMenuItem;
    private JCheckBoxMenuItem coordinatesMenuItem;
    private JMenu recentMenu;
    @Getter
    private JScrollPane mapScrollPane;
    private JTable layerTable;
    private JPopupMenu layerPopupMenu;
    private SmartSplitPane rightSplit;
    private SmartSplitPane mainSplit;
    private SmartSplitPane paletteSplit;
    private BrushPreview brushPreview;
    private JSlider opacitySlider;
    private JLabel zoomLabel;
    @Getter
    private JLabel tilePositionLabel;
    @Getter
    private TimedStatusLabel statusLabel;
    private AbstractButton paintButton;
    private AbstractButton eraseButton;
    private AbstractButton pourButton;
    private AbstractButton eyedButton;
    private AbstractButton marqueeButton;
    private AbstractButton moveButton;
    private AbstractButton objectMoveButton;
    private AbstractButton objectAddButton;
    private AbstractButton objectRemoveButton;
    private TabbedTilesetsPane tabbedTilesetsPane;
    private AboutDialog aboutDialog;
    @Getter
    @Setter
    private MapLayerEdit paintEdit;
    private FloatablePanel layersPanel;
    private FloatablePanel tilesetPanel;
    private ToolSemantic currentToolSemantic;

    private final MapMouseListener mouseListener;

    public MapEditor() {
        MapEditorAction.init(this);
        mouseListener = new MapMouseListener(this);

        objectSelectionToolSemantic = new ObjectSelectionToolSemantic(this);

        curEyed = new Cursor(Cursor.CROSSHAIR_CURSOR);
        curDefault = new Cursor(Cursor.DEFAULT_CURSOR);

        undoHandler = new UndoHandler(this);
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(undoHandler);

        cursorHighlight = new SelectionLayer(1, 1, 1, 1);
        cursorHighlight.select(0, 0);
        cursorHighlight.setVisible(preferences.getBoolean("cursorhighlight", true));

        // Create our frame
        appFrame = new ApplicationFrame();
        appFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                MapEditorAction.exitAction.actionPerformed(null);
            }
        });
        appFrame.setContentPane(createContentPane());
        createMenuBar();
        appFrame.setJMenuBar(mainMenuBar);


        setCurrentTileMap(null);
        updateRecent(null);

        appFrame.setVisible(true);

        //tileInstancePropertiesDialog = new TileInstancePropertiesDialog(this);

        // Restore the state of the main frame. This needs to happen after
        // making the frame visible, otherwise it has no effect (in Linux).
        appFrame.updateExtendedState();

        // Restore the size and position of the layers and tileset panels.
        layersPanel.restore();
        tilesetPanel.restore();

        rightSplit.restore();
        mainSplit.restore();
        paletteSplit.restore();

        // Load plugins
        pluginLoader = PluginClassLoader.getInstance();
        try {
            pluginLoader.readPlugins(null, appFrame);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(appFrame,
                    e.toString(), "Plugin loader",
                    JOptionPane.WARNING_MESSAGE);
        }
        MapHelper.init(pluginLoader);


        // Make sure the map view is redrawn when grid preferences change.
        // todo: move this functionality out of here somehow, but not back into
        // MapView
        final Preferences display = preferences.node("display");
        display.addPreferenceChangeListener(event -> {
            if (mapView == null) return;

            String key = event.getKey();
            if ("gridOpacity".equals(key)) {
                mapView.setGridOpacity(display.getInt("gridOpacity", 255));
            } else if ("gridAntialias".equals(key)) {
                mapView.setAntialiasGrid(display.getBoolean("gridAntialias", true));
            } else if ("gridColor".equals(key)) {
                mapView.setGridColor(new Color(display.getInt("gridColor",
                        MapView.DEFAULT_GRID_COLOR.getRGB())));
            } else if ("showGrid".equals(key)) {
                mapView.setShowGrid(display.getBoolean("showGrid", false));
            }
        });
    }

    public static MapLayer createLayerCopy(MapLayer layer) {
        try {
            return (MapLayer) layer.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JPanel createContentPane() {
        mapScrollPane = new JScrollPane(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setBorder(null);

        // install adjustment listener to set the view center correctly
        // every time the view is moved around
        AdjustmentListener mapScrollPaneAdjustmentListener = e -> {
            if (mapView != null) {
                JScrollBar hsb = mapScrollPane.getHorizontalScrollBar();
                JScrollBar vsb = mapScrollPane.getVerticalScrollBar();
                float wholeX = (float) (hsb.getMaximum() - hsb.getMinimum() - hsb.getVisibleAmount());
                float viewX;
                if (wholeX != 0.0f)
                    viewX = (float) (hsb.getValue()) / wholeX;
                else
                    viewX = 0.5f;

                float wholeY = (float) (vsb.getMaximum() - vsb.getMinimum() - vsb.getVisibleAmount());
                float viewY;
                if (wholeY != 0.0f)
                    viewY = (float) (vsb.getValue()) / wholeY;
                else
                    viewY = 0.5f;

                mapView.setViewCenter(viewX, viewY);
            }
        };
        mapScrollPane.getHorizontalScrollBar().addAdjustmentListener(mapScrollPaneAdjustmentListener);
        mapScrollPane.getVerticalScrollBar().addAdjustmentListener(mapScrollPaneAdjustmentListener);

        createData();
        createStatusBar();

        // todo: Make continuouslayout an option. Because it can be slow, some
        // todo: people may prefer not to have that.
        layersPanel = new FloatablePanel(
                getAppFrame(), dataPanel, Constants.PANEL_LAYERS, "layers");

        rightSplit = new SmartSplitPane(JSplitPane.VERTICAL_SPLIT, true, layersPanel.getContentPane(), null, "rightSplit");
        rightSplit.setOneTouchExpandable(true);
        rightSplit.setResizeWeight(0.5);
        rightSplit.setBorder(null);

        mainSplit = new SmartSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, true, mapScrollPane,
                rightSplit, "mainSplit");
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setResizeWeight(1.0);
        mainSplit.setBorder(null);

        tabbedTilesetsPane = new TabbedTilesetsPane(this);
        tilesetPanel = new FloatablePanel(
                getAppFrame(), tabbedTilesetsPane, Constants.PANEL_TILE_PALETTE,
                "tilesets");
        paletteSplit = new SmartSplitPane(
                JSplitPane.VERTICAL_SPLIT, true, mainSplit, tilesetPanel.getContentPane(), "paletteSplit");
        paletteSplit.setOneTouchExpandable(true);
        paletteSplit.setResizeWeight(1.0);


        // GUI components
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createToolBar(), BorderLayout.WEST);
        mainPanel.add(paletteSplit, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Creates all the menus and submenus of the top menu bar. Handles
     * assigning listeners and tooltips as well.
     */
    private void createMenuBar() {
        JMenuItem save = new TMenuItem(MapEditorAction.saveAction);
        JMenuItem saveAs = new TMenuItem(MapEditorAction.saveAsAction);
        JMenuItem saveAsImage = new TMenuItem(MapEditorAction.saveAsImageAction);
        JMenuItem exportMap = new TMenuItem(MapEditorAction.exportAction);
        JMenuItem close = new TMenuItem(new CloseMapAction(this, MapEditorAction.saveAction));

        recentMenu = new JMenu(Resources.getString("menu.file.recent"));

        MapEventAdapter.addListener(save);
        MapEventAdapter.addListener(saveAs);
        MapEventAdapter.addListener(saveAsImage);
        MapEventAdapter.addListener(exportMap);
        MapEventAdapter.addListener(close);

        FileMenu fileMenu = new FileMenu();
        fileMenu.add(new TMenuItem(new NewMapAction(this, MapEditorAction.saveAction)));
        fileMenu.add(new TMenuItem(new OpenMapAction(this, MapEditorAction.saveAction)));
        fileMenu.add(recentMenu);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(saveAsImage);
        fileMenu.add(exportMap);
        fileMenu.addSeparator();
        fileMenu.add(close);
        fileMenu.add(new TMenuItem(MapEditorAction.exitAction));

        JMenuItem copyMenuItem = new TMenuItem(new CopyAction(this));
        JMenuItem copyAllMenuItem = new TMenuItem(new CopyAllAction(this));
        JMenuItem cutMenuItem = new TMenuItem(new CutAction(this));
        JMenuItem pasteMenuItem = new TMenuItem(new PasteAction(this));
        copyMenuItem.setEnabled(false);
        copyAllMenuItem.setEnabled(false);
        cutMenuItem.setEnabled(false);
        pasteMenuItem.setEnabled(false);

        JMenu transformSub = new JMenu(Resources.getString("menu.edit.transform"));
        transformSub.add(new TMenuItem(MapEditorAction.flipHorAction, true));
        transformSub.add(new TMenuItem(MapEditorAction.flipVerAction, true));
        MapEventAdapter.addListener(transformSub);

        JMenu editMenu = new JMenu(Resources.getString("menu.edit"));
        editMenu.add(new TMenuItem(undoHandler.getUndoAction()));
        editMenu.add(new TMenuItem(undoHandler.getRedoAction()));
        editMenu.addSeparator();
        editMenu.add(copyMenuItem);
        editMenu.add(copyAllMenuItem);
        editMenu.add(cutMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(transformSub);
        editMenu.addSeparator();
        editMenu.add(createMenuItem(Resources.getString("menu.edit.preferences"),
                null, Resources.getString("menu.edit.preferences.tooltip"), null));
        editMenu.add(createMenuItem(Resources.getString("menu.edit.brush"), null,
                Resources.getString("menu.edit.brush.tooltip"),
                "control B"));

        MapEventAdapter.addListener(copyMenuItem);
        MapEventAdapter.addListener(copyAllMenuItem);
        MapEventAdapter.addListener(cutMenuItem);
        MapEventAdapter.addListener(pasteMenuItem);


        JMenu mapMenu = new JMenu(Resources.getString("menu.map"));
        mapMenu.add(createMenuItem(Resources.getString("menu.map.resize"), null,
                Resources.getString("menu.map.resize.tooltip")));
        mapMenu.add(createMenuItem(Resources.getString("menu.map.search"), null,
                Resources.getString("menu.map.search.tooltip")));
        mapMenu.addSeparator();
        mapMenu.add(createMenuItem(Resources.getString("menu.map.properties"), null,
                Resources.getString("menu.map.properties.tooltip")));
        MapEventAdapter.addListener(mapMenu);


        JMenu layerMenu = new JMenu(Resources.getString("menu.layer"));
        JMenuItem layerAdd = new TMenuItem(MapEditorAction.addLayerAction);
        MapEventAdapter.addListener(layerAdd);
        layerMenu.add(layerAdd);
        layerMenu.add(new TMenuItem(MapEditorAction.cloneLayerAction));
        layerMenu.add(new TMenuItem(MapEditorAction.deleteLayerAction));
        layerMenu.addSeparator();
        layerMenu.add(new TMenuItem(MapEditorAction.addObjectGroupAction));
        layerMenu.addSeparator();
        layerMenu.add(new TMenuItem(MapEditorAction.moveLayerUpAction));
        layerMenu.add(new TMenuItem(MapEditorAction.moveLayerDownAction));
        layerMenu.addSeparator();
        layerMenu.add(new TMenuItem(MapEditorAction.mergeLayerDownAction));
        layerMenu.add(new TMenuItem(MapEditorAction.mergeAllLayersAction));
        layerMenu.addSeparator();
        layerMenu.add(MapEditorAction.showLayerPropertiesAction);

        JMenu tilesetMenu = new JMenu(Resources.getString("menu.tilesets"));
        tilesetMenu.add(createMenuItem(
                Resources.getString("menu.tilesets.new"), null,
                Resources.getString("menu.tilesets.new.tooltip")));
        tilesetMenu.add(createMenuItem(
                Resources.getString("menu.tilesets.import"), null,
                Resources.getString("menu.tilesets.import.tooltip")));
        tilesetMenu.addSeparator();
        tilesetMenu.add(createMenuItem(
                Resources.getString("menu.tilesets.refresh"), null,
                Resources.getString("menu.tilesets.refresh.tooltip"), "F5"));
        tilesetMenu.addSeparator();
        tilesetMenu.add(createMenuItem(
                Resources.getString("menu.tilesets.manager"), null,
                Resources.getString("menu.tilesets.manager.tooltip")));


        JMenu selectMenu = new JMenu(Resources.getString("menu.select"));
        selectMenu.add(new TMenuItem(MapEditorAction.selectAllAction, true));
        selectMenu.add(new TMenuItem(MapEditorAction.cancelSelectionAction, true));
        selectMenu.add(new TMenuItem(MapEditorAction.inverseAction, true));

        gridMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.grid"));
        gridMenuItem.addActionListener(this);
        gridMenuItem.setToolTipText(Resources.getString("menu.view.grid.tooltip"));
        gridMenuItem.setAccelerator(KeyStroke.getKeyStroke("control G"));

        cursorMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.cursor"));
        cursorMenuItem.setSelected(preferences.getBoolean("cursorhighlight", true));
        cursorMenuItem.addActionListener(this);
        cursorMenuItem.setToolTipText(Resources.getString("menu.view.cursor.tooltip"));

        JCheckBoxMenuItem boundaryMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.boundaries"));
        boundaryMenuItem.addActionListener(this);
        boundaryMenuItem.setToolTipText(Resources.getString("menu.view.boundaries.tooltip"));
        boundaryMenuItem.setAccelerator(KeyStroke.getKeyStroke("control E"));

        coordinatesMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.coordinates"));
        coordinatesMenuItem.addActionListener(this);
        coordinatesMenuItem.setToolTipText(Resources.getString("menu.view.coordinates.tooltip"));

        JMenu viewMenu = new JMenu(Resources.getString("menu.view"));
        viewMenu.add(new TMenuItem(MapEditorAction.zoomInAction));
        viewMenu.add(new TMenuItem(MapEditorAction.zoomOutAction));
        viewMenu.add(new TMenuItem(MapEditorAction.zoomNormalAction));
        viewMenu.addSeparator();
        viewMenu.add(gridMenuItem);
        viewMenu.add(cursorMenuItem);
        viewMenu.add(coordinatesMenuItem);

        MapEventAdapter.addListener(layerMenu);
        MapEventAdapter.addListener(tilesetMenu);
        MapEventAdapter.addListener(selectMenu);
        MapEventAdapter.addListener(viewMenu);

        JMenu helpMenu = new JMenu(Resources.getString("menu.help"));
        helpMenu.add(createMenuItem(Resources.getString("menu.help.plugins"), null,
                Resources.getString("menu.help.plugins.tooltip")));
        helpMenu.add(createMenuItem(Resources.getString("menu.help.about"), null, Resources.getString("menu.help.about.tooltip")));

        mainMenuBar = new MainMenuBar();
        mainMenuBar.add(fileMenu);
        mainMenuBar.add(editMenu);
        mainMenuBar.add(selectMenu);
        mainMenuBar.add(viewMenu);
        mainMenuBar.add(mapMenu);
        mainMenuBar.add(layerMenu);
        mainMenuBar.add(tilesetMenu);
        //mainMenuBar.add(objectMenu);
        mainMenuBar.add(helpMenu);
    }

    /**
     * Creates the tool bar.
     *
     * @return the created tool bar
     */
    private JToolBar createToolBar() {
        Icon iconMove = Resources.getIcon("icon/gimp-tool-move-22.png");
        Icon iconPaint = Resources.getIcon("icon/gimp-tool-pencil-22.png");
        Icon iconErase = Resources.getIcon("icon/gimp-tool-eraser-22.png");
        Icon iconPour = Resources.getIcon("icon/gimp-tool-bucket-fill-22.png");
        Icon iconEyed = Resources.getIcon("icon/gimp-tool-color-picker-22.png");
        Icon iconMarquee = Resources.getIcon("icon/gimp-tool-rect-select-22.png");
        Icon iconAddObject = Resources.getIcon("icon/gnome-list-add-22.png");
        Icon iconRemoveObject = Resources.getIcon("icon/gnome-list-remove-22.png");
        Icon iconMoveObject = Resources.getIcon("icon/gimp-tool-object-move-22.png");

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

        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(true);
        toolBar.add(moveButton);
        toolBar.add(paintButton);
        toolBar.add(eraseButton);
        toolBar.add(pourButton);
        toolBar.add(eyedButton);
        toolBar.add(marqueeButton);
        toolBar.add(Box.createRigidArea(new Dimension(5, 5)));
        toolBar.add(objectAddButton);
        toolBar.add(objectRemoveButton);
        toolBar.add(objectMoveButton);
        toolBar.add(Box.createRigidArea(new Dimension(0, 5)));
        toolBar.add(new TButton(MapEditorAction.zoomInAction));
        toolBar.add(new TButton(MapEditorAction.zoomOutAction));
        toolBar.add(Box.createRigidArea(new Dimension(5, 5)));
        toolBar.add(Box.createGlue());

        brushPreview = new BrushPreview();
        MapEventAdapter.addListener(brushPreview);
        toolBar.add(brushPreview);

        return toolBar;
    }

    private void createData() {
        dataPanel = new JPanel(new BorderLayout());

        layerPopupMenu = new JPopupMenu();
        layerPopupMenu.add(MapEditorAction.addLayerAction);
        layerPopupMenu.add(MapEditorAction.addObjectGroupAction);
        layerPopupMenu.add(MapEditorAction.cloneLayerAction);
        layerPopupMenu.addSeparator();
        layerPopupMenu.add(MapEditorAction.deleteLayerAction);
        layerPopupMenu.add(MapEditorAction.moveLayerUpAction);
        layerPopupMenu.add(MapEditorAction.moveLayerDownAction);
        layerPopupMenu.addSeparator();
        layerPopupMenu.add(MapEditorAction.mergeLayerDownAction);
        layerPopupMenu.addSeparator();
        layerPopupMenu.add(MapEditorAction.mergeAllLayersAction);
        layerPopupMenu.addSeparator();
        layerPopupMenu.add(MapEditorAction.showLayerPropertiesAction);

        //navigation and tool options
        // TODO: the minimap is prohibitively slow, need to speed this up
        // before it can be used
        MiniMapViewer miniMap = new MiniMapViewer();
        //miniMap.setMainPanel(mapScrollPane);
        JScrollPane miniMapSp = new JScrollPane();
        miniMapSp.getViewport().setView(miniMap);
        miniMapSp.setMinimumSize(new Dimension(0, 120));

        // Layer table
        layerTable = new JTable(new LayerTableModel());
        layerTable.getColumnModel().getColumn(0).setPreferredWidth(32);
        layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerTable.getSelectionModel().addListSelectionListener(this);
        layerTable.addMouseListener(new MouseAdapter() {
            // mouse listener for popup menu in layerTable
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e))
                    return;
                int row = layerTable.rowAtPoint(e.getPoint());
                int col = layerTable.columnAtPoint(e.getPoint());
                layerTable.changeSelection(row, col, false, false);
                layerPopupMenu.show(layerTable, e.getPoint().x, e.getPoint().y);
            }
        });

        // Opacity slider
        opacitySlider = new JSlider(0, 100, 100);
        opacitySlider.addChangeListener(this);
        JLabel opacityLabel = new JLabel(
                Resources.getString("dialog.main.opacity.label"));
        opacityLabel.setLabelFor(opacitySlider);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        sliderPanel.add(opacityLabel);
        sliderPanel.add(opacitySlider);
        sliderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                sliderPanel.getPreferredSize().height));

        // Layer buttons
        AbstractButton layerAddButton = new TButton(MapEditorAction.addLayerAction);
        MapEventAdapter.addListener(layerAddButton);

        JPanel layerButtons = new JPanel();
        layerButtons.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1;
        layerButtons.add(layerAddButton, gridBagConstraints);
        layerButtons.add(new TButton(MapEditorAction.moveLayerUpAction), gridBagConstraints);
        layerButtons.add(new TButton(MapEditorAction.moveLayerDownAction), gridBagConstraints);
        layerButtons.add(new TButton(MapEditorAction.cloneLayerAction), gridBagConstraints);
        layerButtons.add(new TButton(MapEditorAction.deleteLayerAction), gridBagConstraints);
        layerButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                layerButtons.getPreferredSize().height));

        JPanel layerPanel = new JPanel();
        layerPanel.setLayout(new GridBagLayout());
        layerPanel.setPreferredSize(new Dimension(120, 120));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(3, 0, 0, 0);
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.gridy += 1;
        layerPanel.add(sliderPanel, gridBagConstraints);
        gridBagConstraints.weighty = 1;
        gridBagConstraints.gridy += 1;
        layerPanel.add(new JScrollPane(layerTable), gridBagConstraints);
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridy += 1;
        layerPanel.add(layerButtons, gridBagConstraints);

        // Create paint panel
        TilePalettePanel tilePalettePanel = new TilePalettePanel();

        JPanel brushesPanel = new JPanel();

        JTabbedPane paintPanel = new JTabbedPane();
        paintPanel.add("Palette", tilePalettePanel);
        paintPanel.add("Brushes", brushesPanel);
        paintPanel.setSelectedIndex(1);

        JToolBar tabsPanel = new JToolBar();
        tabsPanel.add(paintPanel);

        dataPanel.add(layerPanel);
    }

    private void createStatusBar() {
        statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));

        zoomLabel = new JLabel("100%");
        zoomLabel.setPreferredSize(zoomLabel.getPreferredSize());
        tilePositionLabel = new JLabel(" ", SwingConstants.CENTER);

        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        JPanel largePart = new JPanel();

        statusLabel = new TimedStatusLabel(5);

        statusBar.add(statusLabel);
        statusBar.add(largePart);
        statusBar.add(tilePositionLabel);
        statusBar.add(Box.createRigidArea(new Dimension(20, 0)));
        statusBar.add(zoomLabel);
    }

    private void updateLayerTable() {
        int cl = currentLayer;
        if (layerTable.isEditing()) {
            layerTable.getCellEditor(layerTable.getEditingRow(),
                    layerTable.getEditingColumn()).cancelCellEditing();
        }
        ((LayerTableModel) layerTable.getModel()).setMap(currentTileMap);

        if (currentTileMap != null) {
            if (currentTileMap.getTotalLayers() > 0 && cl == -1) {
                cl = 0;
            }

            setCurrentLayerIndex(cl);
        }

        updateLayerOperations();
    }

    public void updateLayerOperations() {
        int nrLayers = 0;

        if (currentTileMap != null) {
            nrLayers = currentTileMap.getTotalLayers();
        }

        final boolean validSelection = currentLayer >= 0;
        final boolean notBottom = currentLayer > 0;
        final boolean notTop = currentLayer < nrLayers - 1 && validSelection;
        final boolean tileLayer =
                validSelection && getCurrentLayer() instanceof TileLayer;
        final boolean objectGroup =
                validSelection && getCurrentLayer() instanceof ObjectGroup;

        if (validSelection) {
            MapLayer l = getCurrentLayer();
            cursorHighlight.setTileDimensions(l.getTileWidth(), l.getTileHeight());
        }

        paintButton.setEnabled(tileLayer);
        eraseButton.setEnabled(tileLayer);
        pourButton.setEnabled(tileLayer);
        eyedButton.setEnabled(tileLayer);
        moveButton.setEnabled(validSelection);
        objectAddButton.setEnabled(objectGroup);
        objectRemoveButton.setEnabled(objectGroup);
        objectMoveButton.setEnabled(objectGroup);

        MapEditorAction.cloneLayerAction.setEnabled(validSelection);
        MapEditorAction.deleteLayerAction.setEnabled(validSelection);
        MapEditorAction.moveLayerUpAction.setEnabled(notTop);
        MapEditorAction.moveLayerDownAction.setEnabled(notBottom);
        MapEditorAction.mergeLayerDownAction.setEnabled(notBottom);
        MapEditorAction.mergeAllLayersAction.setEnabled(nrLayers > 1);

        opacitySlider.setEnabled(validSelection);
    }

    private JMenuItem createMenuItem(String name, Icon icon, String tipText) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(this);
        if (icon != null) {
            menuItem.setIcon(icon);
        }
        if (tipText != null) {
            menuItem.setToolTipText(tipText);
        }
        return menuItem;
    }

    private JMenuItem createMenuItem(String name, Icon icon, String tipText,
                                     String keyStroke) {
        JMenuItem menuItem = createMenuItem(name, icon, tipText);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke));
        return menuItem;
    }

    private AbstractButton createToggleButton(Icon icon, String command,
                                              String tt) {
        AbstractButton button;
        button = new JToggleButton("", icon);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setActionCommand(command);
        button.addActionListener(this);
        if (tt != null) {
            button.setToolTipText(tt);
        }
        return button;
    }

    /**
     * Returns the currently selected layer.
     *
     * @return the currently selected layer
     */
    public MapLayer getCurrentLayer() {
        return currentTileMap.getLayer(currentLayer);
    }

    /**
     * Returns the currently selected layer index.
     *
     * @return the currently selected layer index
     */
    public int getCurrentLayerIndex() {
        return currentLayer;
    }

    public void setCurrentLayerIndex(int index) {
        if (currentLayer == index)    // no change => no work to do!
            return;

        if (currentTileMap == null) {    // no current map => no layer selected currently
            currentLayer = -1;
            return;
        }

        // boundary check
        int totalLayers = currentTileMap.getTotalLayers();
        if (index < 0 || totalLayers <= index) {
            currentLayer = -1;
            return;
        }
        currentLayer = index;
        layerTable.changeSelection(totalLayers - currentLayer - 1, 0,
                false, false);
        MapLayer l = currentTileMap.getLayer(currentLayer);
        mapView.setCurrentLayer(l);
        Rectangle r = l.getBounds();
        statusLabel.setInfoText(String.format(Constants.STATUS_LAYER_SELECTED_FORMAT, l.getName(), r.width, r.height, r.x, r.y, l.getTileWidth(), l.getTileHeight()));
        cursorHighlight.setParent(getCurrentLayer());

        updateToolSemantics();
    }

    public void updateTileCoordsLabel(Point tile) {
        if (tile != null && currentTileMap.inBounds(tile.x, tile.y)) {
            tilePositionLabel.setText(tile.x + ", " + tile.y);
        } else {
            tilePositionLabel.setText(" ");
        }
    }

    public void updateCursorHighlight(Point tile) {
        boolean highlightActive = tile != null && preferences.getBoolean("cursorhighlight", true);
        cursorHighlight.setVisible(highlightActive);

        if (!highlightActive)
            return;
        Rectangle redraw = cursorHighlight.getBounds();
        Rectangle brushRedraw = currentBrush.getBounds();

        brushRedraw.x = tile.x - brushRedraw.width / 2;
        brushRedraw.y = tile.y - brushRedraw.height / 2;

        if (!redraw.equals(brushRedraw)) {
            if (currentBrush instanceof CustomBrush) {
                CustomBrush customBrush = (CustomBrush) currentBrush;
                ListIterator<MapLayer> layers = customBrush.getLayers();
                while (layers.hasNext()) {
                    MapLayer layer = layers.next();
                    layer.setOffset(brushRedraw.x, brushRedraw.y);
                }
                redraw.width = currentBrush.getBounds().width;
                redraw.height = currentBrush.getBounds().height;
            }
            mapView.repaintRegion(cursorHighlight, redraw);
            cursorHighlight.setOffset(brushRedraw.x, brushRedraw.y);
            //cursorHighlight.selectRegion(currentBrush.getShape());
            mapView.repaintRegion(cursorHighlight, brushRedraw);
        }
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        if ("paint".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_PAINT);
            resetBrush();
        } else if ("erase".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_ERASE);
            resetBrush();
        } else if ("point".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_POINT);
            resetBrush();
        } else if ("pour".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_POUR);
            resetBrush();
        } else if ("eyed".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_EYED);
            resetBrush();
        } else if ("marquee".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_MARQUEE);
            resetBrush();
        } else if ("move".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_MOVE);
            resetBrush();
        } else if ("addobject".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_ADD_OBJ);
            resetBrush();
        } else if ("removeobject".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_REMOVE_OBJ);
            resetBrush();
        } else if ("moveobject".equals(command)) {
            setCurrentPointerState(ActionModesEnum.PS_MOVE_OBJ);
            resetBrush();
        } else {
            handleEvent(event);
        }
    }

    // TODO: Most if not all of the below should be moved into action objects
    private void handleEvent(ActionEvent event) {
        String command = event.getActionCommand();

        if (command.equals(Resources.getString("menu.edit.brush"))) {
            BrushDialog bd = new BrushDialog(this, appFrame, currentBrush);
            bd.setVisible(true);
        } else if (command.equals(Resources.getString("menu.tilesets.new"))) {
            if (currentTileMap != null) {
                NewTilesetDialog dialog =
                        new NewTilesetDialog(appFrame, getCurrentLayer(), undoSupport);
                TileSet newSet = dialog.create();
                if (newSet != null) {
                    currentTileMap.addTileset(newSet);
                }
            }
        } else if (command.equals(Resources.getString("menu.tilesets.import"))) {
            if (currentTileMap != null) {
                JFileChooser chooser = new JFileChooser(currentTileMap.getFilename());
                MapReader[] readers = pluginLoader.getReaders();
                for (MapReader reader : readers) {
                    try {
                        chooser.addChoosableFileFilter(new TiledFileFilter(
                                reader.getFilter(),
                                reader.getName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                chooser.addChoosableFileFilter(
                        new TiledFileFilter(TiledFileFilter.FILTER_TSX));

                int ret = chooser.showOpenDialog(appFrame);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    String filename = chooser.getSelectedFile().getAbsolutePath();
                    try {
                        TileSet set = MapHelper.loadTileset(filename);
                        currentTileMap.addTileset(set);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (command.equals(Resources.getString("menu.tilesets.refresh"))) {
            if (currentTileMap != null) {
                Vector<TileSet> tilesets = currentTileMap.getTilesets();
                for (TileSet tileset : tilesets) {
                    try {
                        tileset.checkUpdate();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(appFrame,
                                e.getLocalizedMessage(),
                                Constants.IMPORT_ERROR_MSG, JOptionPane.WARNING_MESSAGE);
                    }
                }
                mapView.repaint();
                brushPreview.setBrush(currentBrush);
            }
        } else if (command.equals(Resources.getString("menu.tilesets.manager"))) {
            if (currentTileMap != null) {
                TilesetManager manager = new TilesetManager(appFrame, currentTileMap);
                manager.setVisible(true);
            }
        } else if (command.equals(Resources.getString("menu.map.properties"))) {
            PropertiesDialog pd = new MapPropertiesDialog(appFrame,
                    currentTileMap, undoSupport);
            pd.setTitle(Resources.getString("dialog.properties.map.title"));
            pd.getProps();
        } else if (command.equals(Resources.getString("menu.view.boundaries")) ||
                command.equals("Hide Boundaries")) {
            mapView.toggleMode(MapView.PF_BOUNDARY_MODE);
        } else if (command.equals(Resources.getString("menu.view.grid"))) {
            // Toggle grid
            Preferences displayPrefs = preferences.node("display");
            boolean showGrid = displayPrefs.getBoolean("showGrid", false);
            displayPrefs.putBoolean("showGrid", !showGrid);
        } else if (command.equals(Resources.getString("menu.view.coordinates"))) {
            // Toggle coordinates
            mapView.toggleMode(MapView.PF_COORDINATES);
        } else if (command.equals(Resources.getString("menu.view.cursor"))) {
            preferences.putBoolean("cursorhighlight", cursorMenuItem.isSelected());
            cursorHighlight.setVisible(cursorMenuItem.isSelected());
        } else if (command.equals(Resources.getString("menu.map.resize"))) {
            ResizeDialog rd = new ResizeDialog(appFrame, this);
            rd.setVisible(true);
        } else if (command.equals(Resources.getString("menu.map.search"))) {
            SearchDialog sd = new SearchDialog(appFrame, currentTileMap);
            sd.setVisible(true);
        } else if (command.equals(Resources.getString("menu.help.about"))) {
            showAboutDialog();
        } else if (command.equals(Resources.getString("menu.help.plugins"))) {
            PluginDialog pluginDialog =
                    new PluginDialog(appFrame, pluginLoader);
            pluginDialog.setVisible(true);
        } else if (command.equals(Resources.getString("menu.edit.preferences"))) {
            ConfigurationDialog dialog = new ConfigurationDialog(appFrame);
            dialog.configure();
        } else {
            System.out.println(event);
        }
    }

    public void componentHidden(ComponentEvent event) {
    }

    public void componentMoved(ComponentEvent event) {
    }

    public void componentResized(ComponentEvent event) {
        // This can currently only happen when the map changes size
        String s = (int) (mapView.getZoom() * 100) + "%";
        zoomLabel.setText(s);

        // Restore the midpoint
        JViewport mapViewPort = mapScrollPane.getViewport();
        Rectangle viewRect = mapViewPort.getViewRect();
        int absMidX = Math.max(0, Math.round(relativeMidX * mapView.getWidth()) - viewRect.width / 2);
        int absMidY = Math.max(0, Math.round(relativeMidY * mapView.getHeight()) - viewRect.height / 2);
        mapViewPort.setViewPosition(new Point(absMidX, absMidY));
    }

    public void componentShown(ComponentEvent event) {
    }

    public void mapChanged(MapChangedEvent e) {
        if (e.getMap() == currentTileMap) {
            mapScrollPane.setViewportView(mapView);
            updateLayerTable();
            mapView.repaint();
        }
    }

    public void layerAdded(MapChangedEvent e) {
    }

    public void layerRemoved(MapChangedEvent e) {
    }

    public void layerMoved(MapChangedEvent e) {
    }

    public void layerChanged(MapChangedEvent e, MapLayerChangeEvent layerChangeEvent) {
    }

    public void tilesetAdded(MapChangedEvent e, TileSet tileset) {
    }

    public void tilesetRemoved(MapChangedEvent e, int index) {
        mapView.repaint();
    }

    public void tilesetsSwapped(MapChangedEvent e, int index0, int index1) {
    }

    public void valueChanged(ListSelectionEvent e) {
        int selectedRow = layerTable.getSelectedRow();

        // At the moment, this can only be a new layer selection
        if (currentTileMap != null && selectedRow >= 0) {
            setCurrentLayerIndex(currentTileMap.getTotalLayers() - selectedRow - 1);

            float opacity = getCurrentLayer().getOpacity();
            opacitySlider.setValue((int) (opacity * 100));
        } else {
            setCurrentLayerIndex(-1);
        }

        updateLayerOperations();
    }

    public void stateChanged(ChangeEvent e) {
        JViewport mapViewport = mapScrollPane.getViewport();

        if (e.getSource() == opacitySlider) {
            if (currentTileMap != null && currentLayer >= 0) {
                MapLayer layer = getCurrentLayer();
                layer.setOpacity(opacitySlider.getValue() / 100.0f);
            }
        } else if (e.getSource() == mapViewport && mapView != null) {
            // Store the point in the middle for zooming purposes
            Rectangle viewRect = mapViewport.getViewRect();
            relativeMidX = Math.min(1, (viewRect.x + (float) viewRect.width / 2) / (float) mapView.getWidth());
            relativeMidY = Math.min(1, (viewRect.y + (float) viewRect.height / 2) / (float) mapView.getHeight());
        }
    }

    /**
     * Called when the editor is exiting.
     */
    public void shutdown() {
        // Save the extended window state if the window isn't minimized
        final int extendedState = appFrame.getExtendedState();
        final Preferences mainDialogPrefs = preferences.node("dialog/main");
        mainDialogPrefs.putInt("state", extendedState);
        if (extendedState == Frame.NORMAL) {
            mainDialogPrefs.putInt("width", appFrame.getWidth());
            mainDialogPrefs.putInt("height", appFrame.getHeight());
        }

        // Allow the floatable panels to save their position and size
        layersPanel.save();
        tilesetPanel.save();

        mainSplit.save();
        paletteSplit.save();
        rightSplit.save();
    }

    private void showAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(appFrame);
        }
        aboutDialog.setVisible(true);
    }

    private void updateToolSemantics() {
        // FIXME: this is currently very simple, but should be replaced
        // by something that is more powerful - when the tools are refactored
        // and moved out of MapEditor altogether..
        ToolSemantic ts;
        if (currentPointerState == ActionModesEnum.PS_MARQUEE && ObjectGroup.class.isAssignableFrom(getCurrentLayer().getClass())) {
            ts = objectSelectionToolSemantic;
        } else {
            ts = null;
        }
        if (ts == currentToolSemantic) {
            return;
        }
        if (currentToolSemantic != null) {
            currentToolSemantic.deactivate();
        }

        currentToolSemantic = ts;

        if (currentToolSemantic != null) {
            currentToolSemantic.activate();
        }
    }

    public void pour(TileLayer layer, int x, int y,
                      Tile newTile, Tile oldTile) {
        if (newTile == oldTile || !layer.canEdit()) return;

        Rectangle area;
        TileLayer before = (TileLayer) createLayerCopy(layer);
        TileLayer after;

        // Check that the copy was succesfully created
        if (before == null) {
            return;
        }

        if (marqueeSelection == null) {
            area = new Rectangle(new Point(x, y));
            Stack<Point> stack = new Stack<>();

            stack.push(new Point(x, y));
            while (!stack.empty()) {
                // Remove the next tile from the stack
                Point p = stack.pop();

                // If the tile it meets the requirements, set it and push its
                // neighbouring tiles on the stack.
                if (layer.contains(p.x, p.y) &&
                        layer.getTileAt(p.x, p.y) == oldTile) {
                    layer.setTileAt(p.x, p.y, newTile);
                    area.add(p);

                    stack.push(new Point(p.x, p.y - 1));
                    stack.push(new Point(p.x, p.y + 1));
                    stack.push(new Point(p.x + 1, p.y));
                    stack.push(new Point(p.x - 1, p.y));
                }
            }
        } else {
            if (marqueeSelection.getSelectedArea().contains(x, y)) {
                area = marqueeSelection.getSelectedAreaBounds();
                for (int i = area.y; i < area.height + area.y; i++) {
                    for (int j = area.x; j < area.width + area.x; j++) {
                        if (marqueeSelection.getSelectedArea().contains(j, i)) {
                            layer.setTileAt(j, i, newTile);
                        }
                    }
                }
            } else {
                return;
            }
        }

        Rectangle bounds = new Rectangle(
                area.x, area.y, area.width + 1, area.height + 1);
        after = new TileLayer(bounds, layer.getTileWidth(), layer.getTileHeight());
        after.copyFrom(layer);

        MapLayerEdit mle = new MapLayerEdit(layer, before, after);
        mle.setPresentationName(Constants.TOOL_FILL);
        undoSupport.postEdit(mle);
    }

    public void resetBrush() {
        //FIXME: this is an in-elegant hack, but it gets the user out
        //       of custom brush mode
        //(reset the brush if necessary)
        if (currentBrush instanceof CustomBrush) {
            ShapeBrush sb = new ShapeBrush();
            sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
            sb.setTile(currentTile);
            setBrush(sb);
        }
    }

    public void setBrush(AbstractBrush brush) {
        // Make sure a possible current highlight gets erased from screen
        if (mapView != null && preferences.getBoolean("cursorhighlight", true)) {
            Rectangle redraw = cursorHighlight.getBounds();
            mapView.repaintRegion(cursorHighlight, redraw);
        }

        currentBrush = brush;

        // Resize and select the region
        Rectangle brushRedraw = currentBrush.getBounds();
        cursorHighlight.resize(brushRedraw.width, brushRedraw.height, 0, 0);
        cursorHighlight.selectRegion(currentBrush.getShape());
        if (mapView != null) {
            mapView.setBrush(currentBrush);
        }
    }

    public void updateTitle() {
        String title = Resources.getString("dialog.main.title");

        if (currentTileMap != null) {
            String filename = currentTileMap.getFilename();
            title += " - ";
            if (filename != null) {
                title += currentTileMap.getFilename();
            } else {
                title += Resources.getString("general.file.untitled");
            }
            if (unsavedChanges()) {
                title += "*";
            }
        }

        appFrame.setTitle(title);
    }

    /**
     * Checks to see if the undo stack is empty
     *
     * @return <code>true</code> if there is an undo history, <code>false</code> otherwise.
     */
    public boolean unsavedChanges() {
        return currentTileMap != null && undoHandler.canUndo() &&
                !undoHandler.isAllSaved();
    }

    /**
     * Loads a map.
     *
     * @param file filename of map to load
     * @return <code>true</code> if the file was loaded, <code>false</code> if
     * an error occured
     */
    public boolean loadMap(String file) {
        File exist = new File(file);
        if (!exist.exists()) {
            String msg = Resources.getString("general.file.notexists.message");
            JOptionPane.showMessageDialog(appFrame,
                    msg,
                    Resources.getString("dialog.openmap.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            statusLabel.setErrorText(msg);
            return false;
        }

        try {
            TileMap tileMap = MapHelper.loadMap(file);

            if (tileMap != null) {
                setCurrentTileMap(tileMap);
                updateRecent(file);
                statusLabel.setInfoText(Constants.STATUS_FILE_INFO_LOAD_SUCCESS);
                return true;
            } else {
                JOptionPane.showMessageDialog(appFrame,
                        Resources.getString("general.file.failed"),
                        Resources.getString("dialog.openmap.error.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(appFrame,
                    "Error while loading " + file + ": " +
                            e.getLocalizedMessage() + (e.getCause() != null ? "\nCause: " +
                            e.getCause().getLocalizedMessage() : ""),
                    Resources.getString("dialog.openmap.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        statusLabel.setInfoText(Constants.STATUS_FILE_ERROR_LOAD_FAILURE);
        return false;
    }

    public void updateRecent(String filename) {
        // If a filename is given, add it to the recent files
        if (filename != null) {
            TiledConfiguration.addToRecentFiles(filename);
        }

        java.util.List<String> files = TiledConfiguration.getRecentFiles();

        recentMenu.removeAll();

        for (String file : files) {
            recentMenu.add(new TMenuItem(new OpenRecentAction(this, MapEditorAction.saveAction, file)));
        }
    }

    public void setCurrentTileMap(TileMap newTileMap) {
        // Cancel any active selection
        if (marqueeSelection != null && currentTileMap != null) {
            currentTileMap.removeLayerSpecial(marqueeSelection);
        }
        marqueeSelection = null;

        currentTileMap = newTileMap;
        boolean mapLoaded = currentTileMap != null;

        // Create a default brush (protect against a bug with custom brushes)
        ShapeBrush sb = new ShapeBrush();
        sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
        setBrush(sb);

        tabbedTilesetsPane.setMap(currentTileMap);

        if (!mapLoaded) {
            MapEventAdapter.fireEvent(MapEventAdapter.MAP_EVENT_MAP_INACTIVE);
            mapView = null;
            mapScrollPane.setViewportView(Box.createRigidArea(
                    new Dimension(0, 0)));
            setCurrentPointerState(ActionModesEnum.PS_POINT);
            tilePositionLabel.setPreferredSize(null);
            tilePositionLabel.setText(" ");
            zoomLabel.setText(" ");
            setCurrentTile(null);
        } else {
            final Preferences display = preferences.node("display");
            MapEventAdapter.fireEvent(MapEventAdapter.MAP_EVENT_MAP_ACTIVE);
            mapView = MapView.createViewforMap(currentTileMap);

            mapView.addMouseListener(mouseListener);
            mapView.addMouseMotionListener(mouseListener);
            mapView.addMouseWheelListener(mouseListener);

            mapView.addComponentListener(this);
            mapView.setSelectionSet(getSelectionSet());
            mapView.setGridOpacity(display.getInt("gridOpacity", 255));
            mapView.setAntialiasGrid(display.getBoolean("gridAntialias", true));
            mapView.setGridColor(new Color(display.getInt("gridColor",
                    MapView.DEFAULT_GRID_COLOR.getRGB())));
            mapView.setShowGrid(display.getBoolean("showGrid", true));
            JViewport mapViewport = new JViewport();
            mapViewport.setView(mapView);
            mapViewport.addChangeListener(this);
            mapScrollPane.setViewport(mapViewport);
            setCurrentPointerState(ActionModesEnum.PS_PAINT);

            currentTileMap.addMapChangeListener(this);

            gridMenuItem.setState(mapView.getShowGrid());
            coordinatesMenuItem.setState(
                    mapView.getMode(MapView.PF_COORDINATES));

            tilePositionLabel.setText((currentTileMap.getWidth() - 1)
                    + ", " + (currentTileMap.getHeight() - 1));
            tilePositionLabel.setPreferredSize(null);
            Dimension size = tilePositionLabel.getPreferredSize();
            tilePositionLabel.setText(" ");
            tilePositionLabel.setMinimumSize(new Dimension(20, 50));
            tilePositionLabel.setPreferredSize(size);
            zoomLabel.setText(((int) (mapView.getZoom() * 100)) + "%");

            // Get the first non-null tile from the first tileset containing
            // non-null tiles.
            Vector<TileSet> tilesets = currentTileMap.getTilesets();
            Tile firstTile = null;
            if (!tilesets.isEmpty()) {
                Iterator<TileSet> it = tilesets.iterator();
                while (it.hasNext() && firstTile == null) {
                    firstTile = it.next().getFirstTile();
                }
            }
            setCurrentTile(firstTile);

            currentTileMap.addLayerSpecial(cursorHighlight);
        }

        MapEditorAction.zoomInAction.setEnabled(mapLoaded);
        MapEditorAction.zoomOutAction.setEnabled(mapLoaded);
        MapEditorAction.zoomNormalAction.setEnabled(mapLoaded && mapView.getZoomLevel() != ZOOM_NORMAL_SIZE);

        undoHandler.discardAllEdits();
        updateLayerTable();
        updateTitle();
    }

    /**
     * Changes the currently selected tile.
     *
     * @param tile the new tile to be selected
     */
    public void setCurrentTile(Tile tile) {
        resetBrush();

        if (currentTile != tile) {
            currentTile = tile;
            if (currentBrush instanceof ShapeBrush) {
                ((ShapeBrush) currentBrush).setTile(tile);
            }
            brushPreview.setBrush(currentBrush);
        }
    }

    private void setCurrentPointerState(ActionModesEnum state) {
        currentPointerState = state;

        // Select the matching button
        paintButton.setSelected(state == ActionModesEnum.PS_PAINT);
        eraseButton.setSelected(state == ActionModesEnum.PS_ERASE);
        pourButton.setSelected(state == ActionModesEnum.PS_POUR);
        eyedButton.setSelected(state == ActionModesEnum.PS_EYED);
        marqueeButton.setSelected(state == ActionModesEnum.PS_MARQUEE);
        moveButton.setSelected(state == ActionModesEnum.PS_MOVE);
        objectAddButton.setSelected(state == ActionModesEnum.PS_ADD_OBJ);
        objectRemoveButton.setSelected(state == ActionModesEnum.PS_REMOVE_OBJ);
        objectMoveButton.setSelected(state == ActionModesEnum.PS_MOVE_OBJ);

        // Set the matching cursor
        if (mapView != null) {
            switch (currentPointerState) {
                case PS_PAINT:
                case PS_ERASE:
                case PS_POINT:
                case PS_POUR:
                case PS_MARQUEE:
                    mapView.setCursor(curDefault);
                    break;
                case PS_EYED:
                    mapView.setCursor(curEyed);
                    break;
            }
        }

        updateToolSemantics();
    }
}
