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
        JPanel jPanel = new JPanel();
        ProjectPanel projectPanel = new ProjectPanel(jPanel);
        SettingPanel settingPanel = new SettingPanel(jPanel);

        add(jPanel);
    }
}
