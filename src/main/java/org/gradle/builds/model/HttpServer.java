package org.gradle.builds.model;

import java.nio.file.Path;

public class HttpServer implements Component {
    private final Path rootDir;
    private final int port;

    public HttpServer(HttpRepo httpRepo) {
        this.rootDir = httpRepo.getRootDir();
        this.port = httpRepo.getHttpPort();
    }

    public Path getRootDir() {
        return rootDir;
    }

    public int getPort() {
        return port;
    }
}
