package org.gradle.builds.model;

import java.util.List;

public interface JvmLibraryApi extends LibraryApi {
    /**
     * A unique Java identifier for this library.
     */
    String getIdentifier();

    /**
     * A display name for this library.
     */
    String getDisplayName();

    List<JavaClassApi> getApiClasses();
}
