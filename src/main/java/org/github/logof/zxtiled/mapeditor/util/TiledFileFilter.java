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

package org.github.logof.zxtiled.mapeditor.util;

import lombok.Getter;
import org.github.logof.zxtiled.io.PluggableMapIO;
import org.github.logof.zxtiled.io.xml.XMLMapWriter;
import org.github.logof.zxtiled.mapeditor.Resources;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @version $Id$
 */
public class TiledFileFilter extends ConfirmableFileFilter {
    public static final int FILTER_EXT = 0;
    public static final int FILTER_TMX = 1;
    public static final int FILTER_TSX = 2;
    public static final int FILTER_BOTH = 3;
    public static final int FILTER_PLUG = 4;

    private static final String FILETYPE_TILED = Resources.getString("general.filetype.tiled");
    private static final String FILETYPE_TMX = Resources.getString("general.filetype.tiledmap");
    private static final String FILETYPE_TSX = Resources.getString("general.filetype.tiledtileset");
    private static final String FILETYPE_EXT = Resources.getString("general.filetype.byextension");
    private String desc;
    private final LinkedList<String> exts;
    private PluggableMapIO pmio;
    @Getter
    private int type = FILTER_EXT;

    public TiledFileFilter() {
        desc = FILETYPE_TILED;
        exts = new LinkedList<>();
        exts.add("tmx");
        exts.add("tmx.gz");
        exts.add("tsx");
        pmio = new XMLMapWriter();
    }

    public TiledFileFilter(int filter) {
        exts = new LinkedList<>();
        desc = "";
        type = filter;

        if ((filter & FILTER_TMX) != 0) {
            desc = FILETYPE_TMX;
            exts.add("tmx");
            exts.add("tmx.gz");
            pmio = new XMLMapWriter();
        }

        if ((filter & FILTER_TSX) != 0) {
            desc += FILETYPE_TSX;
            exts.add("tsx");
            if (pmio == null) {
                pmio = new XMLMapWriter();
            }
        }

        if (filter == FILTER_EXT) {
            desc = FILETYPE_EXT;
        }
    }

    public TiledFileFilter(PluggableMapIO p) throws Exception {
        exts = new LinkedList<>();
        pmio = p;
        buildFilter(p.getFilter(), p.getName());
    }

    public TiledFileFilter(String filter, String desc) {
        exts = new LinkedList<>();
        buildFilter(filter, desc);
    }

    private void buildFilter(String filter, String desc) {
        this.desc = desc;
        String[] extensions = filter.split(",");
        for (String extension : extensions) {
            exts.add(extension.substring(extension.indexOf('.') + 1));
        }
    }

    public PluggableMapIO getPlugin() {
        return pmio;
    }

    public String getDefaultExtension() {
        if (!exts.isEmpty()) {
            return exts.getFirst();
        } else {
            return null;
        }
    }

    public boolean accept(File file) {
        // todo: Verify that the "!file.exists()" check is rather weird.
        if (type != FILTER_EXT && (file.isFile() || !file.exists())) {
            String fileName = file.getPath().toLowerCase();

            for (String ext : exts) {
                if (fileName.endsWith("." + ext)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public String getDescription() {
        StringBuilder filter = new StringBuilder();

        if (!exts.isEmpty()) {
            filter.append(" (");
            Iterator<String> itr = exts.iterator();
            while (itr.hasNext()) {
                filter.append("*.").append(itr.next());
                if (itr.hasNext()) {
                    filter.append(",");
                }
            }

            filter.append(")");
        }

        return desc + filter;
    }

}
