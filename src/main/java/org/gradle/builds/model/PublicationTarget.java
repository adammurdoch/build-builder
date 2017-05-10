package org.gradle.builds.model;

public class PublicationTarget {
    private final HttpRepository httpRepository;

    public PublicationTarget(HttpRepository httpRepository) {
        this.httpRepository = httpRepository;
    }

    public HttpRepository getHttpRepository() {
        return httpRepository;
    }
}
