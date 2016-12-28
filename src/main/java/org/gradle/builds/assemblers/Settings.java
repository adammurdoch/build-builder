package org.gradle.builds.assemblers;

public class Settings {
    private final int projectCount;
    private final int sourceFileCount;

    public Settings(int projectCount, int sourceFileCount) {
        this.projectCount = projectCount;
        this.sourceFileCount = sourceFileCount;
    }

    public int getProjectCount() {
        return projectCount;
    }

    public int getSourceFileCount() {
        return sourceFileCount;
    }
}
