package org.github.logof.zxtiled.mapeditor.gui;

import lombok.Getter;
import javax.swing.*;
import java.awt.*;

@Getter
public class TilesetPanel {
    private final JPanel contentPane = new JPanel();

    /**
     * @param title the title of this panel
     */
    public TilesetPanel(JComponent child, String title) {
        JLabel titleLabel = new JLabel(title);

        JPanel topPanel = new HeaderPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.LINE_START);

        contentPane.setLayout(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.PAGE_START);
        contentPane.add(child, BorderLayout.CENTER);
    }

    /**
     * The panel that holds the title label and float button.
     */
    private static class HeaderPanel extends JPanel {
        public HeaderPanel(BorderLayout borderLayout) {
            super(borderLayout);
            setBorder(BorderFactory.createEmptyBorder(1, 4, 2, 1));
        }

        protected void paintComponent(Graphics graphics) {
            Color backgroundColor = new Color(200, 200, 240);
            graphics.setColor(backgroundColor);
            ((Graphics2D) graphics).fill(graphics.getClip());
            graphics.setColor(backgroundColor.darker());
            graphics.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
        }
    }
}
