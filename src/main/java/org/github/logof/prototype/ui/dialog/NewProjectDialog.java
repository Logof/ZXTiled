package org.github.logof.prototype.ui.dialog;

import org.github.logof.prototype.ui.panel.ProjectPanel;
import org.github.logof.prototype.ui.panel.SettingPanel;
import javax.swing.*;

public class NewProjectDialog extends JDialog {
    public NewProjectDialog() {
        super();
        initComponent();

    }

    private void initComponent() {
        JPanel jPanel = new JPanel(); // тут доп панель смотрится уместно
        ProjectPanel projectPanel = new ProjectPanel(jPanel);
        SettingPanel settingPanel = new SettingPanel(jPanel);
        add(jPanel);

        // Но если захочется сделать так, то уже что-то не то...
        JPanel jPanel1 = new JPanel();
        ProjectPanel projectPanel1 = new ProjectPanel(jPanel1);

        // А хочется так
        ProjectPanel projectPanel2 = new ProjectPanel(this);
    }


}
