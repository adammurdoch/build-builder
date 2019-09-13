package org.gradle.builds.model;

public class SwiftLibraryApi implements LibraryApi {
    private final SwiftClass apiClass;
    private final String module;

    public SwiftLibraryApi(SwiftClass apiClass, String module) {
        this.apiClass = apiClass;
        this.module = module;
    }

    public SwiftClass getApiClass() {
        return apiClass;
    }

    public String getModule() {
        return module;
    }
}
