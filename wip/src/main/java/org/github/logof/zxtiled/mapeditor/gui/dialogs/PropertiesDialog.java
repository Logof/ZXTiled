package org.github.logof.zxtiled.mapeditor.gui.dialogs;

import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import org.github.logof.zxtiled.mapeditor.undo.ChangePropertiesEdit;
import org.github.logof.zxtiled.mapeditor.util.PropertiesTableModel;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

public class PropertiesDialog extends JDialog {
    private static final String DIALOG_TITLE = Resources.getString("dialog.properties.title");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String DELETE_BUTTON = Resources.getString("general.button.delete");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");
    protected final Properties properties;
    protected final PropertiesTableModel tableModel = new PropertiesTableModel();
    protected JTable propertiesTable;
    protected JPanel mainPanel;
    private final UndoableEditSupport undoSupport;


    /**
     * Creates a new PropertiesDialog instance
     *
     * @param parent     the parent frame that will become the owner of this dialog
     * @param properties the properties to manage
     */
    public PropertiesDialog(JFrame parent, Properties properties, UndoableEditSupport undoSupport) {
        this(parent, properties, undoSupport, true);
    }

    /**
     * Creates a new PropertiesDialog instance
     *
     * @param parent     the parent frame that will become the owner of this dialog
     * @param properties the properties to manage
     * @param doInit     if false, the dialog will not initialise it's UI components,
     *                   but rely on a subclass to to do call init() and pack() eventually after
     *                   the instance has been constructed.
     */
    protected PropertiesDialog(JFrame parent, Properties properties, UndoableEditSupport undoSupport, boolean doInit) {
        super(parent, DIALOG_TITLE, true);
        this.properties = properties;
        this.undoSupport = undoSupport;
        if (doInit) {
            init();
            pack();
            setLocationRelativeTo(getOwner());
        }
    }

    protected void init() {
        propertiesTable = new JTable(tableModel);
        JScrollPane propScrollPane = new JScrollPane(propertiesTable);
        propScrollPane.setPreferredSize(new Dimension(200, 150));

        JButton okButton = new JButton(OK_BUTTON);
        JButton cancelButton = new JButton(CANCEL_BUTTON);

        JPanel user = new VerticalStaticJPanel();
        user.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        user.setLayout(new BoxLayout(user, BoxLayout.LINE_AXIS));
        user.add(Box.createGlue());
        user.add(Box.createRigidArea(new Dimension(5, 0)));

        JPanel buttons = new VerticalStaticJPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(okButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(cancelButton);

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        //mainPanel.add(propScrollPane);
        mainPanel.add(user);
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(okButton);

        //create actionlisteners
        okButton.addActionListener(actionEvent -> {
            UndoableEdit undoableEdit = commit();
            if (undoableEdit != null) {
                undoSupport.postEdit(undoableEdit);
            }
            dispose();
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });
    }

    public void updateInfo() {
        tableModel.setProperties(properties);
    }

    public void getProps() {
        updateInfo();
        setVisible(true);
    }

    public boolean havePropertiesChanged() {
        TableCellEditor editor = propertiesTable.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }

        return !properties.equals(tableModel.getProperties());
    }

    protected UndoableEdit commit() {
        // Make sure there is no active cell editor anymore
        TableCellEditor editor = propertiesTable.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }

        if (havePropertiesChanged()) {
            Properties backup = (Properties) properties.clone();

            // Apply possibly changed properties.
            properties.clear();
            properties.putAll(tableModel.getProperties());

            return new ChangePropertiesEdit(properties, backup);
        } else {
            return null;
        }
    }

    protected void deleteSelected() {
        int total = propertiesTable.getSelectedRowCount();
        Object[] keys = new Object[total];
        int[] selRows = propertiesTable.getSelectedRows();

        for (int i = 0; i < total; i++) {
            keys[i] = propertiesTable.getValueAt(selRows[i], 0);
        }

        for (int i = 0; i < total; i++) {
            if (keys[i] != null) {
                tableModel.remove(keys[i]);
            }
        }
    }
}
