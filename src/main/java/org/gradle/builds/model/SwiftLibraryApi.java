package org.gradle.builds.model;

import java.util.Collections;
import java.util.List;

public class SwiftLibraryApi implements LibraryApi<SwiftClass> {
    private final SwiftClass apiClass;
    private final String module;

    public SwiftLibraryApi(SwiftClass apiClass, String module) {
        this.apiClass = apiClass;
        this.module = module;
    }

    @Override
    public List<SwiftClass> getApiClasses() {
        return Collections.singletonList(apiClass);
    }

    public SwiftClass getApiClass() {
        return apiClass;
    }

    public String getModule() {
        return module;
    }
}
