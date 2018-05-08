package org.gradle.builds.model;

import java.util.ArrayList;
import java.util.List;

public class HasCppSource extends HasSource<CppSourceFile, CppLibraryApi> {
    private final List<CppHeaderFile> implHeaders = new ArrayList<>();
    private final List<CppHeaderFile> publicHeaders = new ArrayList<>();
    private final List<CppHeaderFile> privateHeaders = new ArrayList<>();
    private final List<CppHeaderFile> testHeaders = new ArrayList<>();
    private MacroIncludes macroIncludes = MacroIncludes.none;

    public List<CppHeaderFile> getPublicHeaderFiles() {
        return publicHeaders;
    }

    public CppHeaderFile addPublicHeaderFile(String name) {
        CppHeaderFile headerFile = new CppHeaderFile(name);
        publicHeaders.add(headerFile);
        return headerFile;
    }

    public List<CppHeaderFile> getImplementationHeaderFiles() {
        return implHeaders;
    }

    public CppHeaderFile addImplementationHeaderFile(String name) {
        CppHeaderFile headerFile = new CppHeaderFile(name);
        implHeaders.add(headerFile);
        return headerFile;
    }

    public List<CppHeaderFile> getPrivateHeadersFiles() {
        return privateHeaders;
    }

    public CppHeaderFile addPrivateHeaderFile(String name) {
        CppHeaderFile headerFile = new CppHeaderFile(name);
        privateHeaders.add(headerFile);
        return headerFile;
    }

    public List<CppHeaderFile> getTestHeaders() {
        return testHeaders;
    }

    public CppHeaderFile addTestHeaderFile(String name) {
        CppHeaderFile headerFile = new CppHeaderFile(name);
        testHeaders.add(headerFile);
        return headerFile;
    }

    public CppSourceFile addSourceFile(String name) {
        CppSourceFile sourceFile = new CppSourceFile(name);
        addSourceFile(sourceFile);
        return sourceFile;
    }

    public void setMacroIncludes(MacroIncludes macroIncludes) {
        this.macroIncludes = macroIncludes;
    }

    public MacroIncludes getMacroIncludes() {
        return macroIncludes;
    }
}
