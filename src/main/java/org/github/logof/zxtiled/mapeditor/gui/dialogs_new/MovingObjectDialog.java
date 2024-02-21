package org.github.logof.zxtiled.mapeditor.gui.dialogs_new;

import org.github.logof.zxtiled.core.MapTypeEnum;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.core.objects.MovingObject;
import org.github.logof.zxtiled.mapeditor.enums.MovingObjectTypeEnum;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Vector;

public class MovingObjectDialog extends JDialog {


    private JPanel contentPane;
    private JComboBox<MovingObjectTypeEnum> objectTypeComboBox;
    private JTextField objectName;
    private JSpinner xSpinner;
    private JSpinner ySpinner;
    private JButton buttonCancel;
    private JButton buttonOk;
    private JLabel objectNameLabel;
    private JLabel objectTypeLabel;
    private JLabel xLabel;
    private JSpinner speedSpinner;
    private JLabel yLabel;
    private JPanel movingPanel;

    private final MovingObject movingObject;

    public MovingObjectDialog(MovingObject movingObject, Vector<Tileset> tilesets) {
        this.movingObject = movingObject;
        setContentPane(contentPane);
        initComponent(movingObject);
        initListener();
    }

    private void initListener() {
        buttonOk.addActionListener(actionEvent -> {
            movingObject.setName(objectName.getText());
            movingObject.setType((MovingObjectTypeEnum) objectTypeComboBox.getSelectedItem());
            movingObject.setFinalPoint(new Point((Integer) xSpinner.getValue(), (Integer) ySpinner.getValue()));
            movingObject.setObjectSpeed((Integer) speedSpinner.getValue());
            dispose();
        });
        buttonCancel.addActionListener(actionEvent -> {
            dispose();
        });
    }

    private void initComponent(MovingObject movingObject) {
        this.objectName.setText(movingObject.getName());
        this.objectTypeComboBox.setSelectedItem(movingObject.getType());
        this.xSpinner.setValue(Objects.nonNull(movingObject.getFinalPoint())
                ? movingObject.getFinalPoint().x
                : movingObject.getCoordinateXAt());
        this.ySpinner.setValue(Objects.nonNull(movingObject.getFinalPoint())
                ? movingObject.getFinalPoint().y
                : movingObject.getCoordinateYAt());
        this.speedSpinner.setValue(movingObject.getObjectSpeed());
    }

    private void createUIComponents() {
        this.objectTypeComboBox = new JComboBox<>(MovingObjectTypeEnum.getValuesByMapType(MapTypeEnum.MAP_SIDE_SCROLLED));
    }
}
