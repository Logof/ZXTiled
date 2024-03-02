package org.github.logof.zxtiled.mapeditor.gui.dialogs;

import net.miginfocom.swing.MigLayout;
import org.github.logof.zxtiled.core.MapTypeEnum;
import org.github.logof.zxtiled.core.objects.HotspotObject;
import org.github.logof.zxtiled.core.objects.MapObject;
import org.github.logof.zxtiled.core.objects.MovingObject;
import org.github.logof.zxtiled.mapeditor.Constants;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.enums.HotspotEnum;
import org.github.logof.zxtiled.mapeditor.enums.MovingObjectTypeEnum;
import org.github.logof.zxtiled.mapeditor.gui.IntegerSpinner;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import org.github.logof.zxtiled.mapeditor.undo.ChangeObjectEdit;
import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;

public class ObjectDialog extends PropertiesDialog {
    private static final String DIALOG_TITLE = Resources.getString("dialog.object.title");
    private static final String NAME_LABEL = Resources.getString("dialog.object.name.label");
    private static final String TYPE_LABEL = Resources.getString("dialog.object.type.label");
    private static final String IMAGE_LABEL = Resources.getString("dialog.object.image.label");
    private static final String OBJECT_SPEED = Resources.getString("dialog.object.speed.label");
    private static final String UNTITLED_OBJECT = Resources.getString("general.object.object");
    private static final String BROWSE_BUTTON = Resources.getString("general.button.browse");
    private final MapObject mapObject;
    private JTextField objectName;
    private JComboBox<?> objectType;

    private JSpinner xPointSpinner;
    private JSpinner yPointSpinner;
    private JSpinner speedSpinner;

    public ObjectDialog(JFrame parent, MapObject mapObject, UndoableEditSupport undoSupport) {
        super(parent, mapObject.getProperties(), undoSupport, false);
        this.mapObject = mapObject;
        setTitle(DIALOG_TITLE);
        init();
        pack();
        setLocationRelativeTo(parent);
    }

    public void init() {
        super.init();

        JLabel imageLabel = new JLabel(IMAGE_LABEL);

        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new BoxLayout(miscPropPanel, BoxLayout.PAGE_AXIS));

        miscPropPanel.add(createCommonPanel());
        if (mapObject instanceof MovingObject) {
            miscPropPanel.add(createMovingObjectPanel());
        }
        mainPanel.add(miscPropPanel, 0);
    }

    private JPanel createCommonPanel() {
        JPanel commonPanel = new VerticalStaticJPanel();
        commonPanel.setLayout(new MigLayout());
        commonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // Label's
        JLabel nameLabel = new JLabel(NAME_LABEL);
        JLabel typeLabel = new JLabel(TYPE_LABEL);

        objectName = new JTextField(UNTITLED_OBJECT);

        if (mapObject instanceof MovingObject) {
            objectType = new JComboBox<>(MovingObjectTypeEnum.getValuesByMapType(MapTypeEnum.MAP_SIDE_SCROLLED));
            objectType.addActionListener(e -> ((MovingObject) mapObject)
                    .setType((MovingObjectTypeEnum) ((JComboBox<?>) e.getSource()).getSelectedItem()));
        }

        if (mapObject instanceof HotspotObject) {
            objectType = new JComboBox<>(HotspotEnum.getValuesByMapType(MapTypeEnum.MAP_SIDE_SCROLLED));
            objectType.addActionListener(e -> ((HotspotObject) mapObject)
                    .setType((HotspotEnum) ((JComboBox<?>) e.getSource()).getSelectedItem()));
        }

        commonPanel.add(nameLabel, "gap, sg 1");
        commonPanel.add(objectName, "wrap");
        commonPanel.add(typeLabel, "gap, sg 1");
        commonPanel.add(objectType, "wrap");

        return commonPanel;
    }

    private JPanel createMovingObjectPanel() {
        JPanel movingPanel = new JPanel();
        movingPanel.setLayout(new MigLayout());
        movingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel xLabel = new JLabel("X");
        JLabel yLabel = new JLabel("Y");
        JLabel speedLabel = new JLabel(OBJECT_SPEED);

        xPointSpinner = new IntegerSpinner(mapObject.getCoordinateXAt(), 0, Constants.SCREEN_WIDTH);
        yPointSpinner = new IntegerSpinner(mapObject.getCoordinateYAt(), 0, Constants.SCREEN_HEIGHT);
        speedSpinner = new JSpinner();

        movingPanel.add(xLabel, "gap, sg 1");
        movingPanel.add(xPointSpinner, "gap, sg 1");
        movingPanel.add(yLabel, "gap, sg 1");
        movingPanel.add(yPointSpinner, "gap, sg 1");
        movingPanel.add(speedLabel, "gap, sg 1");
        movingPanel.add(speedSpinner, "gap, sg 1");

        return movingPanel;
    }

    public void updateInfo() {
        super.updateInfo();
        objectName.setText(mapObject.getName());

        if (mapObject instanceof MovingObject) {
            objectType.setSelectedItem(((MovingObject) mapObject).getType());
        }

        if (mapObject instanceof HotspotObject) {
            objectType.setSelectedItem(((HotspotObject) mapObject).getType());
        }
    }

    protected UndoableEdit commit() {
        CompoundEdit ce = new CompoundEdit();
        UndoableEdit propertyEdit = super.commit();
        if (propertyEdit != null) {
            ce.addEdit(propertyEdit);
        }

        // Make sure the changes to the object can be undone
        ce.addEdit(new ChangeObjectEdit(mapObject));

        mapObject.setName(objectName.getText());

        if (mapObject instanceof MovingObject) {
            MovingObject movingObject = (MovingObject) mapObject;
            movingObject.setType((MovingObjectTypeEnum) objectType.getSelectedItem());
            movingObject.setObjectSpeed(2);
            movingObject.setFinalPoint(new Point((Integer) xPointSpinner.getValue(), (Integer) yPointSpinner.getValue()));
        }

        if (mapObject instanceof HotspotObject) {
            ((HotspotObject) mapObject).setType((HotspotEnum) objectType.getSelectedItem());
        }
        ce.end();

        return ce;
    }
}
