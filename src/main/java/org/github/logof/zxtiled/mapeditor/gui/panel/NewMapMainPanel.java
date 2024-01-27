package org.github.logof.zxtiled.mapeditor.gui.panel;

import lombok.Getter;
import org.github.logof.zxtiled.mapeditor.Resources;
import org.github.logof.zxtiled.mapeditor.gui.AbstractPanel;
import org.github.logof.zxtiled.mapeditor.gui.VerticalStaticJPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NewMapMainPanel extends AbstractPanel {
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");

    private NewMapMiscPropPanel miscPropPanel;
    @Getter
    private NewMapSizePanels sizePanels;

    private final ActionListener actionListener;

    private JPanel buttonsPanel;

    public NewMapMainPanel(JDialog parentDialog, ActionListener actionListener) {
        super(parentDialog, true);
        this.actionListener = actionListener;
    }

    @Override
    protected void initComponent() {
        miscPropPanel = new NewMapMiscPropPanel(this);
        sizePanels = new NewMapSizePanels(this);
        buttonsPanel = createCustomButtonsPanel();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(miscPropPanel);
        add(sizePanels);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(Box.createGlue());
        add(buttonsPanel);
    }

    private JPanel createCustomButtonsPanel() {
        buttonsPanel = new JPanel();

        // OK and Cancel buttons
        JButton okButton = new JButton(OK_BUTTON);
        JButton cancelButton = new JButton(CANCEL_BUTTON);
        okButton.addActionListener(actionListener);
        cancelButton.addActionListener(actionListener);


        JPanel buttonsPanel = new VerticalStaticJPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(Box.createGlue());
        buttonsPanel.add(okButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(cancelButton);

        ((JDialog) getParentContainer()).getRootPane().setDefaultButton(okButton);

        return buttonsPanel;
    }
}
