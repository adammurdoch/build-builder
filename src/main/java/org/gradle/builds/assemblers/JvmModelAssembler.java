package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.util.Collections;
import java.util.function.Consumer;

public abstract class JvmModelAssembler extends AbstractModelAssembler {
    private static final JavaLibraryApi slf4jApi = new JavaLibraryApi("slf4j", Collections.singletonList(JavaClassApi.method("org.slf4j.LoggerFactory", "getLogger(\"abc\")")));
    protected static final PublishedLibrary<JavaLibraryApi> slfj4 = new PublishedLibrary<>("slf4j", new ExternalDependencyDeclaration("org.slf4j:slf4j-api:1.7.25"), slf4jApi);

    @Override
    protected void rootProject(Project rootProject) {
        ProjectScriptBlock allProjects = rootProject.getBuildScript().allProjects();
        allProjects.jcenter();
        allProjects.block("tasks.withType(JavaCompile)").property("options.incremental", "true");
    }

    protected void addTests(Project project, HasJavaSource<?> application) {
        for (JavaClass javaClass : application.getSourceFiles()) {
            JavaClass testClass = application.addTest(javaClass.getName() + "Test");
            testClass.addRole(new UnitTest(javaClass));
        }
    }

    protected void addSource(Project project, HasJavaSource<?> component, JavaClass apiClass, Consumer<JavaClass> implClass) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers() - 2);
        String className = project.getQualifiedNamespaceFor() + "." + project.getTypeNameFor();
        project.getClassGraph().visit((Graph.Visitor<JavaClass>) (nodeDetails, dependencies) -> {
            JavaClass javaClass;
            int layer = nodeDetails.getLayer();
            if (layer == 0) {
                javaClass = apiClass;
            } else {
                javaClass = component.addClass(className + "Impl" + nodeDetails.getNameSuffix());
            }
            if (layer == implLayer) {
                for (JvmLibraryApi library : component.getReferencedLibraries()) {
                    javaClass.uses(library.getApiClasses());
                }
                implClass.accept(javaClass);
            }
            for (Dependency<JavaClass> dep : dependencies) {
                javaClass.uses(dep.getTarget());
            }
            return javaClass;
        });
    }
}
