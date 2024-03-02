package org.github.logof.zxtiled.mapeditor.gui.dialogs_new;

import org.github.logof.zxtiled.core.MapTypeEnum;
import org.github.logof.zxtiled.core.Tileset;
import org.github.logof.zxtiled.core.objects.HotspotObject;
import org.github.logof.zxtiled.mapeditor.enums.HotspotEnum;
import javax.swing.*;
import java.util.Objects;
import java.util.Vector;

public class HotspotDialog extends JDialog {


    private JPanel contentPane;
    private JComboBox<HotspotEnum> objectTypeComboBox;
    private JTextField objectName;
    private JButton buttonCancel;
    private JButton buttonOk;
    private JLabel objectNameLabel;
    private JLabel objectTypeLabel;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private final HotspotObject hotspotObject;
    private final Vector<Tileset> tilesets;

    public HotspotDialog(HotspotObject hotspotObject, Vector<Tileset> tilesets) {
        this.hotspotObject = hotspotObject;
        this.tilesets = tilesets;
        setContentPane(contentPane);
        initComponent(hotspotObject);
        initListener();


        if (Objects.nonNull(tilesets) && Objects.nonNull(hotspotObject.getType())) {

        }
    }

    private void initListener() {
        buttonOk.addActionListener(actionEvent -> {
            hotspotObject.setName(objectName.getText());
            hotspotObject.setType((HotspotEnum) objectTypeComboBox.getSelectedItem());
            dispose();
        });
        buttonCancel.addActionListener(actionEvent -> {
            dispose();
        });
    }

    private void initComponent(HotspotObject movingObject) {
        this.objectName.setText(movingObject.getName());
        this.objectTypeComboBox.setSelectedItem(movingObject.getType());

    }

    private void createUIComponents() {
        this.objectTypeComboBox = new JComboBox<>(HotspotEnum.getValuesByMapType(MapTypeEnum.MAP_SIDE_SCROLLED));
    }
}
