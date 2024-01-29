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
import org.github.logof.zxtiled.core.ObjectLayer;
import org.github.logof.zxtiled.core.PointerStateManager;
import org.github.logof.zxtiled.core.Tile;
import org.github.logof.zxtiled.core.TileLayer;
import org.github.logof.zxtiled.core.TileMap;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.io.MapHelper;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.brush.AbstractBrush;
import org.github.logof.zxtiled.mapeditor.brush.CustomBrush;
import org.github.logof.zxtiled.mapeditor.brush.ShapeBrush;
import org.github.logof.zxtiled.mapeditor.enums.PointerStateEnum;
import org.github.logof.zxtiled.mapeditor.gui.ApplicationFrame;
import org.github.logof.zxtiled.mapeditor.gui.SmartSplitPane;
import org.github.logof.zxtiled.mapeditor.gui.StatusBar;
import org.github.logof.zxtiled.mapeditor.gui.TabbedTilesetsPane;
import org.github.logof.zxtiled.mapeditor.gui.TilePalettePanel;
import org.github.logof.zxtiled.mapeditor.gui.TilesetPanel;
import org.github.logof.zxtiled.mapeditor.gui.ToolBar;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.AboutDialog;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.ConfigurationDialog;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.MapPropertiesDialog;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.NewTilesetDialog;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.PropertiesDialog;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.ResizeDialog;
import org.github.logof.zxtiled.mapeditor.gui.dialogs.TilesetManager;
import org.github.logof.zxtiled.mapeditor.gui.menu.MainMenuBar;
import org.github.logof.zxtiled.mapeditor.gui.panel.MapLayersPanel;
import org.github.logof.zxtiled.mapeditor.listener.MapEditorActionListener;
import org.github.logof.zxtiled.mapeditor.listener.MapEditorComponentListener;
import org.github.logof.zxtiled.mapeditor.listener.MapEditorListSelectionListener;
import org.github.logof.zxtiled.mapeditor.listener.MapEditorMapChangeListener;
import org.github.logof.zxtiled.mapeditor.listener.MapEditorMouseListener;
import org.github.logof.zxtiled.mapeditor.selection.ObjectSelectionToolSemantic;
import org.github.logof.zxtiled.mapeditor.selection.SelectionLayer;
import org.github.logof.zxtiled.mapeditor.selection.SelectionSet;
import org.github.logof.zxtiled.mapeditor.undo.MapLayerEdit;
import org.github.logof.zxtiled.mapeditor.undo.UndoHandler;
import org.github.logof.zxtiled.mapeditor.util.LayerTableModel;
import org.github.logof.zxtiled.mapeditor.util.MapEventAdapter;
import org.github.logof.zxtiled.mapeditor.util.TiledFileFilter;
import org.github.logof.zxtiled.util.TiledConfiguration;
import org.github.logof.zxtiled.view.MapView;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;
import java.awt.event.ActionEvent;
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
public class MapEditor {
    /**
     * Current release version.
     */
    public static final String version = "0.0.1";

    public static final Preferences preferences = TiledConfiguration.root();

    @Getter
    private final UndoHandler undoHandler;
    @Getter
    private final UndoableEditSupport undoSupport;
    @Getter
    private final SelectionLayer cursorHighlight;
    @Getter
    private final ApplicationFrame appFrame;
    @Getter
    private final ObjectSelectionToolSemantic objectSelectionToolSemantic;
    @Getter
    private final SelectionSet selectionSet = new SelectionSet();
    @Getter
    private TileMap currentTileMap;
    @Getter
    private MapView mapView;
    @Getter
    private Tile currentTile;
    @Getter
    @Setter
    private MapObject currentObject = null;
    @Getter
    private int currentLayerIndex = -1;

    @Getter
    private AbstractBrush currentBrush;
    @Getter
    @Setter
    private SelectionLayer marqueeSelection;
    @Getter
    @Setter
    private MapLayer clipboardLayer;
    @Getter
    @Setter
    private float relativeMidX;
    @Getter
    @Setter
    private float relativeMidY;

    private JPanel dataPanel;

    @Getter
    private StatusBar statusBar;
    private final MainMenuBar mainMenuBar;

    private JCheckBoxMenuItem cursorMenuItem;

    @Getter
    private JScrollPane mapScrollPane;

    @Getter
    private JTable layerTable;
    private JPopupMenu layerPopupMenu;
    private SmartSplitPane rightSplit;
    private SmartSplitPane mainSplit;
    private SmartSplitPane paletteSplit;

    private TabbedTilesetsPane tabbedTilesetsPane;
    private AboutDialog aboutDialog;
    @Getter
    @Setter
    private MapLayerEdit paintEdit;
    private MapLayersPanel mapLayersPanel;
    private TilesetPanel tilesetPanel;


    private final MapEditorMouseListener mouseListener;

    @Getter
    private static MapEditorActionListener actionListener;

    @Getter
    private final PointerStateManager pointerStateManager;

    @Getter
    private final ToolBar toolBar;

    private final ListSelectionListener listSelectionListener;
    private final MapChangeListener mapChangeListener;
    private final ComponentListener componentListener;
    public MapEditor() {
        MapEditorAction.init(this);
        mouseListener = new MapEditorMouseListener(this);
        actionListener = new MapEditorActionListener(this);
        listSelectionListener = new MapEditorListSelectionListener(this);

        toolBar = new ToolBar();

        pointerStateManager = new PointerStateManager(this);

        objectSelectionToolSemantic = new ObjectSelectionToolSemantic(this);

        mapChangeListener = new MapEditorMapChangeListener(this);
        componentListener = new MapEditorComponentListener(this);

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
        //new MapEditorMainPanel(appFrame);
        mainMenuBar = new MainMenuBar();
        appFrame.setJMenuBar(mainMenuBar);


        //setCurrentTileMap(null);
        //updateRecent(null);

        appFrame.setVisible(true);

        //tileInstancePropertiesDialog = new TileInstancePropertiesDialog(this);

        // Restore the state of the main frame. This needs to happen after
        // making the frame visible, otherwise it has no effect (in Linux).
        appFrame.updateExtendedState();

        // Restore the size and position of the layers and tileset panels.

        //rightSplit.restore();
        //mainSplit.restore();
        //paletteSplit.restore();

        // Make sure the map view is redrawn when grid preferences change.
        // todo: move this functionality out of here somehow, but not back into MapView
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

        createData();
        statusBar = new StatusBar();

        // todo: Make continuous layout an option. Because it can be slow, some people may prefer not to have that.
        mapLayersPanel = new MapLayersPanel(dataPanel, Constants.PANEL_LAYERS);
        rightSplit = new SmartSplitPane(JSplitPane.VERTICAL_SPLIT, true, mapLayersPanel.getContentPane(),
                null, "rightSplit");
        rightSplit.setOneTouchExpandable(true);
        rightSplit.setResizeWeight(0.5);
        rightSplit.setBorder(null);

        mainSplit = new SmartSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, mapScrollPane,
                rightSplit, "mainSplit");
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setResizeWeight(1.0);
        mainSplit.setBorder(null);

        tabbedTilesetsPane = new TabbedTilesetsPane(this);
        tilesetPanel = new TilesetPanel(tabbedTilesetsPane, Constants.PANEL_TILE_PALETTE);
        paletteSplit = new SmartSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainSplit,
                tilesetPanel.getContentPane(), "paletteSplit");
        paletteSplit.setOneTouchExpandable(true);
        paletteSplit.setResizeWeight(0.8);

        // GUI components
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toolBar, BorderLayout.LINE_START);
        mainPanel.add(paletteSplit, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.PAGE_END);

        return mainPanel;
    }

    /**
     * Creates the tool bar.
     */

    private void createData() {
        dataPanel = new JPanel(new BorderLayout());

        layerPopupMenu = new JPopupMenu();
        layerPopupMenu.add(MapEditorAction.showLayerPropertiesAction);

        //navigation and tool options
        // Layer table
        layerTable = new JTable(new LayerTableModel());
        layerTable.getColumnModel().getColumn(0).setPreferredWidth(32);
        layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerTable.getSelectionModel().addListSelectionListener(listSelectionListener);
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

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        sliderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                sliderPanel.getPreferredSize().height));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1;

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



    public void updateLayerTable() {
        int currentLayerIndex = this.currentLayerIndex;
        if (layerTable.isEditing()) {
            layerTable.getCellEditor(layerTable.getEditingRow(),
                    layerTable.getEditingColumn()).cancelCellEditing();
        }
        ((LayerTableModel) layerTable.getModel()).setMap(currentTileMap);

        if (currentTileMap != null) {
            if (currentTileMap.getTotalLayers() > 0 && currentLayerIndex == -1) {
                currentLayerIndex = 0;
            }

            setCurrentLayerIndex(currentLayerIndex);
        }

        updateLayerOperations();
    }

    public void updateLayerOperations() {
        final boolean validSelection = currentLayerIndex >= 0;
        final boolean tileLayer = validSelection && getCurrentLayer() instanceof TileLayer;
        final boolean objectGroup = validSelection && getCurrentLayer() instanceof ObjectLayer;

        toolBar.updateTileLayerOperations(tileLayer);
        toolBar.updateValidSelectionOperations(validSelection);
        toolBar.updateObjectGroupOperations(objectGroup);
    }

    /**
     * Returns the currently selected layer.
     *
     * @return the currently selected layer
     */
    public MapLayer getCurrentLayer() {
        return currentTileMap.getLayer(currentLayerIndex);
    }

    public void setCurrentLayerIndex(int index) {
        if (currentLayerIndex == index) {    // no change => no work to do!
            return;
        }

        if (currentTileMap == null) {    // no current map => no layer selected currently
            currentLayerIndex = -1;
            return;
        }

        // boundary check
        int totalLayers = currentTileMap.getTotalLayers();
        if (index < 0 || totalLayers <= index) {
            currentLayerIndex = -1;
            return;
        }
        currentLayerIndex = index;
        layerTable.changeSelection(totalLayers - currentLayerIndex - 1, 0,
                false, false);
        MapLayer mapLayer = currentTileMap.getLayer(currentLayerIndex);
        mapView.setCurrentLayer(mapLayer);
        Rectangle layerBounds = mapLayer.getBounds();
        statusBar.getStatusLabel()
                 .setInfoText(String.format(Constants.STATUS_LAYER_SELECTED_FORMAT, mapLayer.getName(), layerBounds.width, layerBounds.height, layerBounds.x, layerBounds.y, mapLayer.getTileWidth(), mapLayer.getTileHeight()));
        cursorHighlight.setParent(getCurrentLayer());

        pointerStateManager.updateToolSemantics();
    }

    public void updateTileCoordsLabel(Point tile) {
        if (tile != null && currentTileMap.inBounds(tile.x, tile.y)) {
            statusBar.getTilePositionLabel().setText(tile.x + ", " + tile.y);
        } else {
            statusBar.getTilePositionLabel().setText(" ");
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


    // TODO: Most if not all of the below should be moved into action objects
    public void handleEvent(ActionEvent event) {
        String command = event.getActionCommand();

        if (command.equals(Resources.getString("menu.tilesets.new"))) {
            if (currentTileMap != null) {
                NewTilesetDialog dialog =
                        new NewTilesetDialog(appFrame, getCurrentLayer(), undoSupport);
                Tileset newSet = dialog.create();
                if (newSet != null) {
                    currentTileMap.addTileset(newSet);
                }
            }
        } else if (command.equals(Resources.getString("menu.tilesets.import"))) {
            if (currentTileMap != null) {
                JFileChooser chooser = new JFileChooser(currentTileMap.getFilename());
                chooser.addChoosableFileFilter(new TiledFileFilter(TiledFileFilter.FILTER_TSX));

                int ret = chooser.showOpenDialog(appFrame);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    String filename = chooser.getSelectedFile().getAbsolutePath();
                    try {
                        Tileset set = MapHelper.loadTileset(filename);
                        currentTileMap.addTileset(set);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (command.equals(Resources.getString("menu.tilesets.refresh"))) {
            if (currentTileMap != null) {
                Vector<Tileset> tilesets = currentTileMap.getTilesets();
                for (Tileset tileset : tilesets) {
                    try {
                        tileset.checkUpdate();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(appFrame,
                                e.getLocalizedMessage(),
                                Constants.IMPORT_ERROR_MSG, JOptionPane.WARNING_MESSAGE);
                    }
                }
                mapView.repaint();
                ToolBar.getBrushPreview().setBrush(currentBrush);
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
        } else if (command.equals(Resources.getString("menu.help.about"))) {
            showAboutDialog();
        } else if (command.equals(Resources.getString("menu.edit.preferences"))) {
            ConfigurationDialog dialog = new ConfigurationDialog(appFrame);
            dialog.configure();
        } else {
            System.out.println(event);
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


    public void pour(TileLayer layer, int x, int y,
                      Tile newTile, Tile oldTile) {
        if (newTile == oldTile || layer.cannotEdit()) return;

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
     */
    public void loadMap(String file) {
        File exist = new File(file);
        if (!exist.exists()) {
            String msg = Resources.getString("general.file.notexists.message");
            JOptionPane.showMessageDialog(appFrame,
                    msg,
                    Resources.getString("dialog.openmap.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            statusBar.getStatusLabel().setErrorText(msg);
            //return false;
        }

        try {
            TileMap tileMap = MapHelper.loadMap(file);

            if (tileMap != null) {
                setCurrentTileMap(tileMap);
                updateRecent(file);
                statusBar.getStatusLabel().setInfoText(Constants.STATUS_FILE_INFO_LOAD_SUCCESS);
                //return true;
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

        statusBar.getStatusLabel().setInfoText(Constants.STATUS_FILE_ERROR_LOAD_FAILURE);
        //return false;
    }

    //TODO как-то нужно вызывать из FileMenu
    public void updateRecent(String filename) {
        // If a filename is given, add it to the recent files
        if (filename != null) {
            TiledConfiguration.addToRecentFiles(filename);
        }

        java.util.List<String> files = TiledConfiguration.getRecentFiles();


        // НЕ УДАЛЯТЬ
        //recentMenu.removeAll();
        // НЕ УДАЛЯТЬ
        /*for (String file : files) {
            recentMenu.add(new TMenuItem(new OpenRecentAction(this, MapEditorAction.saveAction, file)));
        }*/
    }

    public void setCurrentTileMap(TileMap tileMap) {
        // Cancel any active selection
        if (marqueeSelection != null && currentTileMap != null) {
            currentTileMap.removeLayerSpecial(marqueeSelection);
        }
        marqueeSelection = null;

        currentTileMap = tileMap;
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
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_POINT);
            statusBar.getTilePositionLabel().setPreferredSize(null);
            statusBar.getTilePositionLabel().setText(" ");
            statusBar.getZoomLabel().setText(" ");
            setCurrentTile(null);
        } else {
            final Preferences display = preferences.node("display");
            MapEventAdapter.fireEvent(MapEventAdapter.MAP_EVENT_MAP_ACTIVE);
            mapView = MapView.createViewforMap(currentTileMap);

            mapView.addMouseListener(mouseListener);
            mapView.addMouseMotionListener(mouseListener);
            mapView.addMouseWheelListener(mouseListener);

            mapView.addComponentListener(componentListener);
            mapView.setSelectionSet(getSelectionSet());
            mapView.setGridOpacity(display.getInt("gridOpacity", 255));
            mapView.setAntialiasGrid(display.getBoolean("gridAntialias", true));
            mapView.setGridColor(new Color(display.getInt("gridColor",
                    MapView.DEFAULT_GRID_COLOR.getRGB())));
            mapView.setShowGrid(display.getBoolean("showGrid", true));
            pointerStateManager.setCurrentPointerState(PointerStateEnum.PS_PAINT);

            currentTileMap.addMapChangeListener(mapChangeListener);

            mainMenuBar.getViewMenu().getGridMenuItem().setState(mapView.getShowGrid());
            mainMenuBar.getViewMenu().getCoordinatesMenuItem().setState(mapView.getMode(MapView.PF_COORDINATES));

            statusBar.getTilePositionLabel().setText((currentTileMap.getWidth() - 1)
                    + ", " + (currentTileMap.getHeight() - 1));
            statusBar.getTilePositionLabel().setPreferredSize(null);
            Dimension size = statusBar.getTilePositionLabel().getPreferredSize();
            statusBar.getTilePositionLabel().setText(" ");
            statusBar.getTilePositionLabel().setMinimumSize(new Dimension(20, 50));
            statusBar.getTilePositionLabel().setPreferredSize(size);
            statusBar.getZoomLabel().setText(((int) (mapView.getZoom() * 100)) + "%");

            // Get the first non-null tile from the first tileset containing
            // non-null tiles.
            Vector<Tileset> tilesets = currentTileMap.getTilesets();
            Tile firstTile = null;
            if (!tilesets.isEmpty()) {
                Iterator<Tileset> it = tilesets.iterator();
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
            ToolBar.getBrushPreview().setBrush(currentBrush);
        }
    }
}
