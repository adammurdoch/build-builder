package org.gradle.builds.model;

public class SwiftLibrary extends HasSwiftSource {
    private SwiftClass apiClass;

    public void setApiClass(SwiftClass apiClass) {
        this.apiClass = apiClass;
    }

    public SwiftClass getApiClass() {
        return apiClass;
    }
}
