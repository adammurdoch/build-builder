package org.gradle.builds.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Some source class that references other classes.
 *
 * @param <REF> The type of classes referenced by this class.
 */
public abstract class SourceClass<REF> {
    private final String name;
    private final List<Dependency<REF>> referencedClasses = new ArrayList<>();
    private final Set<ClassRole> roles = new HashSet<>();

    protected SourceClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Dependency<REF>> getReferencedClasses() {
        return referencedClasses;
    }

    public void uses(Dependency<REF> refClass) {
        referencedClasses.add(refClass);
    }

    public <T extends ClassRole> T role(Class<T> type) {
        for (ClassRole role : roles) {
            if (type.isInstance(role)) {
                return type.cast(role);
            }
        }
        return null;
    }

    public void addRole(ClassRole role) {
        roles.add(role);
    }

}
