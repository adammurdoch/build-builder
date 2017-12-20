package org.gradle.builds.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HttpServerImplementation implements Component {
    private final Path rootDir;
    private final int port;
    private final List<Path> sourceBuilds = new ArrayList<>();

    public HttpServerImplementation(HttpRepository httpRepository) {
        this.rootDir = httpRepository.getRootDir();
        this.port = httpRepository.getHttpPort();
    }

    public List<Path> getSourceBuilds() {
        return sourceBuilds;
    }

    public void addSourceBuild(Path sourceDir) {
        sourceBuilds.add(sourceDir);
    }

    public Path getRootDir() {
        return rootDir;
    }

    public int getPort() {
        return port;
    }
}
