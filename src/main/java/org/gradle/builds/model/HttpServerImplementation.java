package org.gradle.builds.model;

import java.nio.file.Path;

public class HttpServerImplementation implements Component {
    private final Path rootDir;
    private final int port;

    public HttpServerImplementation(HttpRepository httpRepository) {
        this.rootDir = httpRepository.getRootDir();
        this.port = httpRepository.getHttpPort();
    }

    public Path getRootDir() {
        return rootDir;
    }

    public int getPort() {
        return port;
    }
}
