package org.github.logof.zxtiled.mapeditor.gui;

import javax.swing.*;
import java.util.Objects;

public abstract class AbstractPanel extends JPanel {
    private final JPanel parentPanel;
    private final JFrame parentFrame;
    private final JDialog parentDialog;

    /*
    public AbstractPanel() {
        this.parentDialog = null;
        this.parentPanel = null;
        this.parentFrame = null;
        initComponent();
    }*/

    public AbstractPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.parentDialog = null;
        this.parentFrame = null;
        initComponent();
        addition();
    }

    public AbstractPanel(JFrame parentFrame) {
        this.parentDialog = null;
        this.parentPanel = null;
        this.parentFrame = parentFrame;
        initComponent();
        addition();
    }

    public AbstractPanel(JDialog parentDialog) {
        this.parentDialog = parentDialog;
        this.parentPanel = null;
        this.parentFrame = null;
        initComponent();
        addition();
    }

    protected abstract void initComponent();

    private void addition() {
        if (Objects.nonNull(parentPanel)) {
            parentPanel.add(this);
        }
        if (Objects.nonNull(parentFrame)) {
            parentFrame.add(this);
        }
        if (Objects.nonNull(parentDialog)) {
            parentDialog.add(this);
        }
    }
}
