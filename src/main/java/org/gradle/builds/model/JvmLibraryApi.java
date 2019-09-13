package org.gradle.builds.model;

public interface JvmLibraryApi extends LibraryApi<JavaClassApi> {
    /**
     * A unique Java identifier for this library.
     */
    String getIdentifier();

    /**
     * A display name for this library.
     */
    String getDisplayName();
}
