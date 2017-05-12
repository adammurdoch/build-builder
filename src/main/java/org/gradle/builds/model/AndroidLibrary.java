package org.gradle.builds.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class AndroidLibrary extends AndroidComponent implements JvmLibrary {
    private JavaClassApi rClass;
    private JavaClass activity;

    @Override
    public Set<JavaClassApi> getApi() {
        return new LinkedHashSet<>(Arrays.asList(activity.getApi(), rClass));
    }

    public void setRClass(JavaClassApi rClass) {
        this.rClass = rClass;
    }

    public JavaClass getActivity() {
        return activity;
    }

    public void setActivity(JavaClass apiClass) {
        this.activity = apiClass;
    }
}
