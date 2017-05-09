package org.gradle.builds.model;

import java.util.Collections;
import java.util.Set;

public class JavaClassApi {
    private final String name;
    private final Set<String> methods;
    private final Set<String> fields;

    public JavaClassApi(String name, Set<String> methods, Set<String> fields) {
        this.name = name;
        this.methods = methods;
        this.fields = fields;
    }

    public static JavaClassApi method(String className, String method) {
        return new JavaClassApi(className, Collections.singleton(method), Collections.emptySet());
    }

    public static JavaClassApi field(String className, String field) {
        return new JavaClassApi(className, Collections.emptySet(), Collections.singleton(field));
    }

    public String getName() {
        return name;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public Set<String> getFields() {
        return fields;
    }
}
