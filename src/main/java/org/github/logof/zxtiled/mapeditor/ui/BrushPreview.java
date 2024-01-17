/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package org.github.logof.zxtiled.mapeditor.ui;

import lombok.Setter;
import org.github.logof.zxtiled.mapeditor.brush.AbstractBrush;
import javax.swing.*;
import java.awt.*;

/**
 * @version $Id$
 */
@Setter
public class BrushPreview extends JPanel {
    private AbstractBrush brush;

    public BrushPreview() {
        setPreferredSize(new Dimension(22, 22));
    }

    public void paint(Graphics graphics) {
        if (brush != null) {
            brush.drawPreview((Graphics2D) graphics, getSize(), null);
        }
    }
}
