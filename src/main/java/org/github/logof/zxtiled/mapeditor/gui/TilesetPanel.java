package org.github.logof.zxtiled.mapeditor.gui;

import lombok.Getter;
import org.github.logof.zxtiled.util.TiledConfiguration;
import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class TilesetPanel {
    private final JLabel titleLabel;
    private final JComponent child;
    private final Preferences prefs;
    @Getter
    private final JPanel contentPane = new JPanel();
    private boolean visible = true;

    /**
     * Constructs a floatable panel with the given title. When the panel is
     * floated, it is placed in a {@link JDialog} with <code>parent</code> as
     * its parent.
     *
     * @param title         the title of this panel
     * @param preferencesId the unique identifier for this panel
     */
    public TilesetPanel(JComponent child, String title, String preferencesId) {
        this.child = child;
        titleLabel = new JLabel(title);
        prefs = TiledConfiguration.node("dock/" + preferencesId);


        JPanel topPanel = new HeaderPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Start in non-floating state
        // todo: Immediately restore floating state when found in preferences
        contentPane.setLayout(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(child, BorderLayout.CENTER);
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        contentPane.setVisible(visible);
        if (visible) {
            contentPane.add(child);
        } else {
            contentPane.remove(child);
        }
        prefs.putBoolean("visible", visible);
    }

    /**
     * Restore the state from the preferences.
     */
    public void restore() {
        setVisible(prefs.getBoolean("visible", visible));
    }


    /**
     * Sets a new title for this panel.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * The panel that holds the title label and float button.
     */
    private static class HeaderPanel extends JPanel {
        public HeaderPanel(BorderLayout borderLayout) {
            super(borderLayout);
            setBorder(BorderFactory.createEmptyBorder(1, 4, 2, 1));
        }

        protected void paintComponent(Graphics g) {
            Color backgroundColor = new Color(200, 200, 240);
            g.setColor(backgroundColor);
            ((Graphics2D) g).fill(g.getClip());
            g.setColor(backgroundColor.darker());
            g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
        }
    }
}
