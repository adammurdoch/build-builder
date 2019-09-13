package org.gradle.builds.model;

import java.util.Collections;
import java.util.List;

public class CppLibraryApi implements LibraryApi<CppClass> {
    private final CppClass apiClass;
    private final CppHeaderFile apiHeader;

    public CppLibraryApi(CppClass apiClass, CppHeaderFile apiHeader) {
        this.apiClass = apiClass;
        this.apiHeader = apiHeader;
    }

    @Override
    public List<CppClass> getApiClasses() {
        return Collections.singletonList(apiClass);
    }

    public CppClass getApiClass() {
        return apiClass;
    }

    public CppHeaderFile getApiHeader() {
        return apiHeader;
    }
}
