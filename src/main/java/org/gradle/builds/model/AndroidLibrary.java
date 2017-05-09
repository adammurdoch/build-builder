package org.gradle.builds.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class AndroidLibrary extends AndroidComponent implements JvmLibrary {
    private JavaClassApi rClass;
    private JavaClass apiClass;

    @Override
    public Set<JavaClassApi> getApi() {
        return new LinkedHashSet<>(Arrays.asList(apiClass.getApi(), rClass));
    }

    public void setRClass(JavaClassApi rClass) {
        this.rClass = rClass;
    }

    public void setApiClass(JavaClass apiClass) {
        this.apiClass = apiClass;
    }
}
