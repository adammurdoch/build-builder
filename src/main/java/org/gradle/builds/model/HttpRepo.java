package org.gradle.builds.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class HttpRepo {
    private final Path rootDir;
    private final int httpPort;

    public HttpRepo(Path rootDir, int httpPort) {
        this.rootDir = rootDir;
        this.httpPort = httpPort;
    }

    public URI getUri() {
        try {
            return new URI("http", null, "localhost", httpPort, "/", null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getRootDir() {
        return rootDir;
    }

    public int getHttpPort() {
        return httpPort;
    }
}
