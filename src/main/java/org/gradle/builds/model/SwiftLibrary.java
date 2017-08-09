package org.gradle.builds.model;

public class SwiftLibrary extends HasSwiftSource {
    private SwiftClass apiClass;
    private String module;

    public SwiftLibrary(boolean swiftPm) {
        super(swiftPm);
    }

    public void setApiClass(SwiftClass apiClass) {
        this.apiClass = apiClass;
    }

    public SwiftClass getApiClass() {
        return apiClass;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModule() {
        return module;
    }
}
