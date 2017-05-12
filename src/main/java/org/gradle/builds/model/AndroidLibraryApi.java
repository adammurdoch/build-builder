package org.gradle.builds.model;

import java.util.Arrays;
import java.util.List;

public class AndroidLibraryApi implements JvmLibraryApi {
    private final String projectName;
    private final JavaClassApi activityClass;
    private final JavaClassApi rClass;

    public AndroidLibraryApi(String projectName, JavaClassApi activityClass, JavaClassApi rClass) {
        this.projectName = projectName;
        this.activityClass = activityClass;
        this.rClass = rClass;
    }

    @Override
    public String getIdentifier() {
        return projectName;
    }

    @Override
    public String getDisplayName() {
        return "project " + projectName;
    }

    public JavaClassApi getActivity() {
        return activityClass;
    }

    @Override
    public List<JavaClassApi> getApiClasses() {
        return Arrays.asList(activityClass, rClass);
    }
}
