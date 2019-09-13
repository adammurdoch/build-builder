package org.gradle.builds.model;

public class SwiftApplication extends HasSwiftSource {
    public SwiftApplication(boolean swiftPm, String module) {
        super(swiftPm, module);
    }
}
