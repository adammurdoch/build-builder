package org.gradle.builds.model;

public class SwiftLibrary extends HasSwiftSource {
    private SwiftClass apiClass;

    public SwiftLibrary(boolean swiftPm) {
        super(swiftPm);
    }

    public void setApiClass(SwiftClass apiClass) {
        this.apiClass = apiClass;
    }

    public SwiftLibraryApi getApi() {
        return new SwiftLibraryApi(apiClass, getModule());
    }

    public SwiftClass getApiClass() {
        return apiClass;
    }
}
