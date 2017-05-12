package org.gradle.builds.model;

import java.util.Arrays;
import java.util.List;

public class AndroidLibraryApi implements JvmLibraryApi {
    private final JavaClassApi activityClass;
    private final JavaClassApi rClass;

    public AndroidLibraryApi(JavaClassApi activityClass, JavaClassApi rClass) {
        this.activityClass = activityClass;
        this.rClass = rClass;
    }

    public JavaClassApi getActivity() {
        return activityClass;
    }

    @Override
    public List<JavaClassApi> getApiClasses() {
        return Arrays.asList(activityClass, rClass);
    }
}
