package org.gradle.builds.model;

public class AndroidLibrary extends AndroidComponent implements JvmLibrary {
    private final String projectName;
    private JavaClassApi rClass;
    private JavaClass activity;

    public AndroidLibrary(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public AndroidLibraryApi getApi() {
        return new AndroidLibraryApi(projectName, activity.getApi(), rClass);
    }

    public void setRClass(JavaClassApi rClass) {
        this.rClass = rClass;
    }

    public void setActivity(JavaClass apiClass) {
        this.activity = apiClass;
    }
}
