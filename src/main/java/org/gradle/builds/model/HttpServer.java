package org.gradle.builds.model;

import java.nio.file.Path;

public class HttpServer implements Component {
    private Path rootDir;
    private int port;

    public Path getRootDir() {
        return rootDir;
    }

    public void setRootDir(Path rootDir) {
        this.rootDir = rootDir;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
