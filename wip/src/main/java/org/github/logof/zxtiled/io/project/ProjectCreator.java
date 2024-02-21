package org.github.logof.zxtiled.io.project;

import java.io.File;
import java.net.URISyntaxException;

public class ProjectCreator {

    private static final String[] PROJECT_FOLDERS = {"bin", "dev", "gfx", "mus", "script"};

    private final String basicPath;

    public ProjectCreator() throws URISyntaxException {
        this.basicPath = new File(ProjectCreator.class.getProtectionDomain().getCodeSource().getLocation()
                                                      .toURI()).getPath();
    }

    public ProjectCreator(String basicPath) {
        this.basicPath = basicPath;
    }

    private void createProjectSkeleton() {

    }
}
