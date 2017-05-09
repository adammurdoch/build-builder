package org.gradle.builds.model;

import java.util.Collections;
import java.util.Set;

public class JavaLibrary extends HasJavaSource implements JvmLibrary {
    private String targetJavaVersion;
    private JavaClass apiClass;

    public String getTargetJavaVersion() {
        return targetJavaVersion;
    }

    public void setTargetJavaVersion(String targetJavaVersion) {
        this.targetJavaVersion = targetJavaVersion;
    }

    @Override
    public Set<JavaClassApi> getApi() {
        return Collections.singleton(apiClass.getApi());
    }

    public void setApiClass(JavaClass apiClass) {
        this.apiClass = apiClass;
    }
}
