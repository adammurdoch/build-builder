package org.gradle.builds.model;

public class SwiftLibrary extends HasSwiftSource implements HasApi {
    private SwiftClass apiClass;

    public SwiftLibrary(boolean swiftPm, String apiClass, String moduleName) {
        super(swiftPm, moduleName);
        this.apiClass = new SwiftClass(apiClass);
    }

    @Override
    public SwiftLibraryApi getApi() {
        return new SwiftLibraryApi(apiClass, getModule());
    }

    public SwiftClass getApiClass() {
        return apiClass;
    }
}
