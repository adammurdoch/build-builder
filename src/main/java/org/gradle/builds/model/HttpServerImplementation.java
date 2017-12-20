package org.gradle.builds.model;

import java.nio.file.Path;

public class HttpServerImplementation implements Component {
    private final Path rootDir;
    private final int port;
    private final Path sourceBuild;

    public HttpServerImplementation(HttpRepository httpRepository, Path sourceBuild) {
        this.rootDir = httpRepository.getRootDir();
        this.port = httpRepository.getHttpPort();
        this.sourceBuild = sourceBuild;
    }

    public Path getSourceBuild() {
        return sourceBuild;
    }

    public Path getRootDir() {
        return rootDir;
    }

    public int getPort() {
        return port;
    }
}
