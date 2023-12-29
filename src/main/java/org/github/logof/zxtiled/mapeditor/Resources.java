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

package org.github.logof.zxtiled.mapeditor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class implements static accessors to common editor resources. These
 * currently include icons and internationalized strings.
 *
 * @version $Id$
 */
public final class Resources {
    // The resource bundle used by this class
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("gui");

    // Prevent instanciation
    private Resources() {
    }

    /**
     * Retrieves a string from the resource bundle in the default locale.
     *
     * @param key the key for the desired string
     * @return the string for the given key
     */
    public static String getString(String key) {
        return resourceBundle.getString(key);
    }

    /**
     * Loads an image from the resources directory. This directory is part of
     * the distribution jar.
     *
     * @param filename the filename relative from the resources directory
     * @return A BufferedImage instance of the image
     */
    public static Optional<Image> getImage(String filename) {
        InputStream inputStream = Resources.class.getResourceAsStream("/" + filename);
        if (Objects.isNull(inputStream)) {
            return Optional.empty();
        }
        try {
            return Optional.of(ImageIO.read(inputStream));
        } catch (IOException e) {
            System.out.println("Failed to load as image: " + filename);
            return Optional.empty();
        }
    }

    /**
     * Loads the image using {@link #getImage(String)} and uses it to create
     * a new {@link ImageIcon} instance.
     *
     * @param filename the filename of the image relative from the
     *                 <code>resources</code> directory
     * @return the loaded icon, or <code>null</code> when an error occured
     * while loading the image
     */
    public static Icon getIcon(String filename) {
        return new ImageIcon(getImage(filename).orElse(null));
    }
}