package org.gradle.builds.model;

public class CppLibrary extends HasCppSource {
    private CppClass apiClass;
    private CppHeaderFile apiHeader;

    public CppClass getApiClass() {
        return apiClass;
    }

    public void setApiClass(CppClass apiClass) {
        this.apiClass = apiClass;
    }

    public CppHeaderFile getApiHeader() {
        return apiHeader;
    }

    public void setApiHeader(CppHeaderFile apiHeader) {
        this.apiHeader = apiHeader;
    }
}
