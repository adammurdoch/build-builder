package org.gradle.builds.model;

public class CppLibrary extends HasCppSource implements HasApi {
    private final CppClass apiClass;
    private final CppHeaderFile apiHeader;

    public CppLibrary(String apiClass, String apiHeader) {
        this.apiClass = new CppClass(apiClass);
        this.apiHeader = addPublicHeaderFile(apiHeader);
    }

    @Override
    public CppLibraryApi getApi() {
        return new CppLibraryApi(apiClass, apiHeader);
    }

    public CppClass getApiClass() {
        return apiClass;
    }

    public CppHeaderFile getApiHeader() {
        return apiHeader;
    }
}
