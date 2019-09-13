package org.gradle.builds.model;

public class AndroidApplication extends AndroidComponent {
    public AndroidApplication(Project project) {
        super(project.getQualifiedNamespaceFor());
    }
}
