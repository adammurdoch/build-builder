package org.gradle.builds.model;

public class CppLibraryApi {
    private final CppClass apiClass;
    private final CppHeaderFile apiHeader;

    public CppLibraryApi(CppClass apiClass, CppHeaderFile apiHeader) {
        this.apiClass = apiClass;
        this.apiHeader = apiHeader;
    }

    public CppClass getApiClass() {
        return apiClass;
    }

    public CppHeaderFile getApiHeader() {
        return apiHeader;
    }
}
