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

package org.github.logof.zxtiled.core;

import lombok.Getter;
import lombok.Setter;
import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * @version $Id$
 */
public class Sprite {
    @Getter
    private Vector<KeyFrame> keys;
    @Getter
    @Setter
    private int borderWidth = 0;
    @Setter
    private int fpl = 0;
    @Setter
    private int totalKeys = -1;

    private float currentFrame = 0;
    @Getter
    private Rectangle frameSize;
    private boolean bPlaying = true;
    @Getter
    private KeyFrame currentKey = null;

    public Sprite() {
        frameSize = new Rectangle();
        keys = new Vector<>();
    }

    public Sprite(Tile[] frames) {
        setFrames(frames);
    }

    public Sprite(Image image, int fpl, int border, int totalFrames) {
        Tile[] frames = null;
        this.fpl = fpl;
        borderWidth = border;

        //TODO: break up the image into tiles

        //given this information, extrapolate the rest...

        frameSize.width = image.getWidth(null) / (fpl + borderWidth * fpl);
        frameSize.height = (int) (image.getHeight(null) / (Math.ceil(totalFrames / fpl) + Math.ceil(totalFrames / fpl) * borderWidth));
        createKey("", frames, KeyFrame.KEY_LOOP);
    }

    public void setFrames(Tile[] frames) {
        frameSize = new Rectangle(0, 0, frames[0].getWidth(), frames[0].getHeight());

        createKey("", frames, KeyFrame.KEY_LOOP);
    }

    public void setFrameSize(int w, int h) {
        frameSize.width = w;
        frameSize.height = h;
    }

    public int getTotalFrames() {
        int total = 0;
        for (KeyFrame key : keys) {
            total += key.getTotalFrames();
        }
        return total;
    }

    public Tile getCurrentFrame() {
        return currentKey.getFrame((int) currentFrame);
    }

    public void setCurrentFrame(float c) {
        if (c < 0) {
            switch (currentKey.flags & KeyFrame.MASK_ANIMATION) {
                case KeyFrame.KEY_LOOP:
                    currentFrame = currentKey.getLastFrame();
                    break;
                case KeyFrame.KEY_AUTO:
                    currentKey = getPreviousKey();
                    currentFrame = currentKey.getLastFrame();
                    break;
                case KeyFrame.KEY_REVERSE:
                    currentKey.setFrameRate(-currentKey.getFrameRate());
                    currentFrame = 0;
                    break;
                case KeyFrame.KEY_STOP:
                    bPlaying = false;
                    currentFrame = 0;
                    break;
            }
        } else if (c > currentKey.getLastFrame()) {
            switch (currentKey.flags & KeyFrame.MASK_ANIMATION) {
                case KeyFrame.KEY_LOOP:
                    currentFrame = 0;
                    break;
                case KeyFrame.KEY_AUTO:
                    currentFrame = 0;
                    currentKey = getNextKey();
                    break;
                case KeyFrame.KEY_REVERSE:
                    currentKey.setFrameRate(-currentKey.getFrameRate());
                    currentFrame = currentKey.getLastFrame();
                    break;
                case KeyFrame.KEY_STOP:
                    bPlaying = false;
                    currentFrame = currentKey.getLastFrame();
                    break;
            }
        } else {
            currentFrame = c;
        }
    }

    public KeyFrame getNextKey() {
        Iterator<KeyFrame> itr = keys.iterator();
        while (itr.hasNext()) {
            KeyFrame k = itr.next();
            if (k == currentKey) {
                if (itr.hasNext()) {
                    return itr.next();
                }
            }
        }

        return keys.get(0);
    }

    public KeyFrame getPreviousKey() {
        //TODO: this
        return null;
    }

    public int getFPL() {
        return fpl;
    }

    public int getTotalKeys() {
        return keys.size();
    }

    public void setKeyFrameTo(String name) {
        for (KeyFrame key : keys) {
            if (key.equalsIgnoreCase(name)) {
                currentKey = key;
                break;
            }
        }
    }

    public void addKey(KeyFrame k) {
        keys.add(k);
    }

    public void removeKey(String name) {
        keys.remove(getKey(name));
    }

    public void createKey(String name, Tile[] frames, int flags) {
        KeyFrame kf = new KeyFrame(name, frames);
        kf.setName(name);
        kf.setFlags(flags);
        addKey(kf);
    }

    public void iterateFrame() {

        if (currentKey != null) {
            if (bPlaying) {
                setCurrentFrame(currentFrame + currentKey.getFrameRate());
            }
        }
    }

    /**
     * Sets the current frame relative to the starting frame of the
     * current key.
     *
     * @param c
     */
    public void keySetFrame(int c) {
        setCurrentFrame(c);
    }

    public void play() {
        bPlaying = true;
    }

    public void stop() {
        bPlaying = false;
    }

    public void keyStepBack(int amt) {
        setCurrentFrame(currentFrame - amt);
    }

    public void keyStepForward(int amt) {
        setCurrentFrame(currentFrame + amt);
    }

    public KeyFrame getKey(String keyName) {
        for (KeyFrame k : keys) {
            if (k != null && k.equalsIgnoreCase(keyName)) {
                return k;
            }
        }
        return null;
    }

    public KeyFrame getKey(int i) {
        return keys.get(i);
    }

    public Rectangle getCurrentFrameRect() {
        int x = 0, y = 0;

        if (frameSize.height > 0 && frameSize.width > 0) {
            y = ((int) currentFrame / fpl) * (frameSize.height + borderWidth);
            x = ((int) currentFrame % fpl) * (frameSize.width + borderWidth);
        }

        return new Rectangle(x, y, frameSize.width, frameSize.height);
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "Frame: (" + frameSize.width + "x" + frameSize.height + ")\n" +
                "Border: " + borderWidth + "\n" +
                "FPL: " + fpl + "\n" +
                "Total Frames: " + getTotalFrames() + "\n" +
                "Total keys: " + totalKeys;
    }

    public class KeyFrame {
        public static final int MASK_ANIMATION = 0x0000000F;

        public static final int KEY_LOOP = 0x01;
        public static final int KEY_STOP = 0x02;
        public static final int KEY_AUTO = 0x04;
        public static final int KEY_REVERSE = 0x08;

        public static final int KEY_NAME_LENGTH_MAX = 32;

        @Setter
        @Getter
        private String name = null;
        @Setter
        @Getter
        private int id = -1;
        @Setter
        @Getter
        private int flags = KEY_LOOP;
        @Setter
        @Getter
        private float frameRate = 1.0f;   //one fps
        private Tile[] frames;

        public KeyFrame() {
            flags = KEY_LOOP;
        }

        public KeyFrame(String name) {
            this();
            this.name = name;
        }

        public KeyFrame(String name, Tile[] tile) {
            this(name);
            frames = tile;
        }

        public int getLastFrame() {
            return frames.length - 1;
        }

        public boolean isFrameLast(int frame) {
            return frames.length - 1 == frame;
        }

        public Tile getFrame(int f) {
            if (f > 0 && f < frames.length) {
                return frames[f];
            }
            return null;
        }

        public int getTotalFrames() {
            return frames.length;
        }

        public boolean equalsIgnoreCase(String n) {
            return name != null && name.equalsIgnoreCase(n);
        }

        public String toString() {
            return "(" + name + ")" + id + ": @ " + frameRate;
        }
    }
}

