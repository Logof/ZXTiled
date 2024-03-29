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

package org.github.logof.zxtiled.mapeditor.gui;

import lombok.Setter;
import javax.swing.*;
import java.awt.*;

/**
 * Tiled button extends on JButton in that it allows for not accepting
 * any text from an attached action. It also doesn't display any margins.
 *
 * @version $Id$
 */
@Setter
public class TButton extends JButton {
    private boolean showText;

    public TButton() {
        setMargin(new Insets(0, 0, 0, 0));
    }

    public TButton(boolean showText) {
        this();
        this.showText = showText;
    }

    public TButton(Action action) {
        this(action, false);
    }

    public TButton(Action action, boolean showText) {
        this(showText);
        setAction(action);
    }

    public void setText(String text) {
        if (showText) {
            super.setText(text);
        }
    }
}
