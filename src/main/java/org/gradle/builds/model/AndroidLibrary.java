package org.gradle.builds.model;

public class AndroidLibrary extends AndroidComponent implements JvmLibrary {
    private JavaClassApi rClass;
    private JavaClass activity;

    @Override
    public AndroidLibraryApi getApi() {
        return new AndroidLibraryApi(activity.getApi(), rClass);
    }

    public void setRClass(JavaClassApi rClass) {
        this.rClass = rClass;
    }

    public void setActivity(JavaClass apiClass) {
        this.activity = apiClass;
    }
}
