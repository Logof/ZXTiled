package org.github.logof.zxtiled.mapeditor.gui.panel;

import lombok.Getter;
import org.github.logof.zxtiled.mapeditor.actions.MapEditorAction;
import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import org.github.logof.zxtiled.mapeditor.gui.TilePalettePanel;
import org.github.logof.zxtiled.mapeditor.util.LayerTableModel;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Getter
public class MapEditorDataPanel extends AbstractPanel {
    private final ListSelectionListener listSelectionListener;
    private JTable layerTable;


    public MapEditorDataPanel(JPanel parentPanel, ListSelectionListener listSelectionListener) {
        super(parentPanel);
        this.listSelectionListener = listSelectionListener;
    }

    @Override
    protected void initComponent() {
        setLayout(new BorderLayout());

        JPopupMenu layerPopupMenu = new JPopupMenu();
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
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }
                int row = layerTable.rowAtPoint(e.getPoint());
                int col = layerTable.columnAtPoint(e.getPoint());
                layerTable.changeSelection(row, col, false, false);
                layerPopupMenu.show(layerTable, e.getPoint().x, e.getPoint().y);
            }
        });

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        sliderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, sliderPanel.getPreferredSize().height));

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

        add(layerPanel);
    }
}
