package org.gradle.builds.assemblers;

import org.gradle.builds.model.MacroIncludes;

public class CppSettings extends Settings {
    private final int headers;
    private final MacroIncludes macroIncludes;

    public CppSettings(int projectCount, int sourceFileCount, int headers, MacroIncludes macroIncludes) {
        super(projectCount, sourceFileCount);
        this.headers = headers;
        this.macroIncludes = macroIncludes;
    }

    public int getHeaders() {
        return headers;
    }

    public MacroIncludes getMacroIncludes() {
        return macroIncludes;
    }
}
