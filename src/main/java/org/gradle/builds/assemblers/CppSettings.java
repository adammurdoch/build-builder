package org.gradle.builds.assemblers;

import org.gradle.builds.model.MacroIncludes;

public class CppSettings extends Settings {
    private final int headers;
    private final MacroIncludes macroIncludes;
    private final boolean boost;

    public CppSettings(int projectCount, int sourceFileCount, int headers, MacroIncludes macroIncludes, boolean boost) {
        super(projectCount, sourceFileCount);
        this.headers = headers;
        this.macroIncludes = macroIncludes;
        this.boost = boost;
    }

    public boolean isBoost() {
        return boost;
    }

    public int getHeaders() {
        return headers;
    }

    public MacroIncludes getMacroIncludes() {
        return macroIncludes;
    }
}
