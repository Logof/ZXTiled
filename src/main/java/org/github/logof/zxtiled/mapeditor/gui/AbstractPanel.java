package org.github.logof.zxtiled.mapeditor.gui;

import lombok.Getter;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public abstract class AbstractPanel extends JPanel {

    @Getter
    private final Container parentContainer;
    private final boolean contentPane;

    public AbstractPanel() {
        this.parentContainer = null;
        this.contentPane = false;
        initComponent();
    }

    public AbstractPanel(Container parentContainer) {
        this.parentContainer = parentContainer;
        this.contentPane = false;
        initComponent();
        addition();
    }

    public AbstractPanel(Container parentContainer, boolean contentPane) {
        this.parentContainer = parentContainer;
        this.contentPane = contentPane;
        initComponent();
        addition();
    }

    protected abstract void initComponent();

    protected void addition() {
        if (Objects.isNull(parentContainer)) {
            return;
        }

        if (contentPane) {
            ((JDialog) parentContainer).setContentPane(this);
        } else {
            parentContainer.add(this);
        }
    }
}
