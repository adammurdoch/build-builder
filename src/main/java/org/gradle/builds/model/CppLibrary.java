package org.gradle.builds.model;

public class CppLibrary extends HasCppSource implements Library<CppLibraryApi> {
    private CppClass apiClass;
    private CppHeaderFile apiHeader;

    public CppLibraryApi getApi() {
        return new CppLibraryApi(apiClass, apiHeader);
    }

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
