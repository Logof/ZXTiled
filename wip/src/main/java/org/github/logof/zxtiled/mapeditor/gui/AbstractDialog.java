package org.github.logof.zxtiled.mapeditor.gui;

import javax.swing.*;
import java.util.Objects;

public abstract class AbstractDialog extends JDialog {
    protected final JFrame parentFrame;

    public AbstractDialog(JFrame parentFrame, String title) {
        super(parentFrame, title, true);
        this.parentFrame = parentFrame;
        initComponent();
    }

    ;

    public AbstractDialog(JFrame parentFrame) {
        super(parentFrame);
        this.parentFrame = parentFrame;
        initComponent();
        addition();
    }

    protected abstract void initComponent();

    private void addition() {
        if (Objects.nonNull(parentFrame)) {
            parentFrame.add(this);
        }
    }
}
