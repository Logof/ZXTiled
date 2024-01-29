package org.github.logof.zxtiled.mapeditor.gui.panel.project;

import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import javax.swing.*;

import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createTitledBorder;

public class GeneralSettingsPanel extends AbstractPanel {

    public GeneralSettingsPanel(JDialog parentDialog) {
        super(parentDialog);
    }

    @Override
    protected void initComponent() {
        JLabel projectNameLabel = new JLabel("Project name:");
        JLabel mapTypeLabel = new JLabel("Map type:");
        JLabel projectMapHeightLabel = new JLabel("Height");
        JLabel projectMapWidthLabel = new JLabel("Width");
        JLabel gameMapTypeLabel = new JLabel("Map type:");
        JComboBox<String> mapTypeComboBox = new JComboBox<>();

        JRadioButton jRadioButton1 = new JRadioButton();
        JRadioButton jRadioButton2 = new JRadioButton();
        JButton projectFolderSaveButton = new JButton("...");
        JTextField projectNameField = new JTextField();
        JTextField projectMapWidthField = new JTextField();
        JTextField projectMapHeightField = new JTextField();

        setBorder(createCompoundBorder(createEmptyBorder(0, 5, 5, 5), createTitledBorder("Project")));
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(projectNameLabel)
                                                      .addComponent(mapTypeLabel)
                                                      .addComponent(projectMapHeightLabel)
                                                      .addComponent(projectMapWidthLabel)
                                                      .addComponent(gameMapTypeLabel))
                                      .addGap(47, 47, 47)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(mapTypeComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                      .addGroup(layout.createSequentialGroup()
                                                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                      .addComponent(jRadioButton2)
                                                                                      .addComponent(projectMapWidthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                                      .addComponent(projectMapHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                                      .addComponent(jRadioButton1))
                                                                      .addGap(0, 0, Short.MAX_VALUE))
                                                      .addGroup(layout.createSequentialGroup()
                                                                      .addComponent(projectNameField)
                                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addComponent(projectFolderSaveButton)))
                                      .addContainerGap())
                                 );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                      .addComponent(projectNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(projectNameLabel)
                                                      .addComponent(projectFolderSaveButton))
                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                      .addComponent(mapTypeLabel)
                                                      .addComponent(mapTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                      .addComponent(projectMapHeightLabel)
                                                      .addComponent(projectMapHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                      .addComponent(projectMapWidthLabel)
                                                      .addComponent(projectMapWidthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(gameMapTypeLabel)
                                                      .addComponent(jRadioButton1))
                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(jRadioButton2)
                                      .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                               );
    }
}
