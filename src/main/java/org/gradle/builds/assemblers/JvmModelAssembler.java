package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

public abstract class JvmModelAssembler extends AbstractModelAssembler {
    protected static final JavaClassApi slf4jApi = JavaClassApi.method("org.slf4j.LoggerFactory", "getLogger(\"abc\")");
    protected static final PublishedJvmLibrary slfj4 = new PublishedJvmLibrary(new ExternalDependencyDeclaration("org.slf4j:slf4j-api:1.7.25"), slf4jApi);

    @Override
    protected void rootProject(Project rootProject) {
        ProjectScriptBlock allProjects = rootProject.getBuildScript().allProjects();
        allProjects.jcenter();
        allProjects.block("tasks.withType(JavaCompile)").property("options.incremental", "true");
    }

    protected void addTests(Project project, HasJavaSource application) {
        for (JavaClass javaClass : new LinkedHashSet<>(application.getSourceFiles())) {
            JavaClass testClass = application.addClass(javaClass.getName() + "Test");
            testClass.addRole(new UnitTest(javaClass));
        }
    }

    protected <T extends HasJavaSource> void addSource(Project project, T component, JavaClass apiClass, Consumer<JavaClass> implClass) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers().size() - 2);
        String className = javaPackageFor(project) + "." + classNameFor(project);
        project.getClassGraph().visit((Graph.Visitor<JavaClass>) (nodeDetails, dependencies) -> {
            JavaClass javaClass;
            int layer = nodeDetails.getLayer();
            int item = nodeDetails.getItem();
            if (layer == 0) {
                javaClass = apiClass;
            } else {
                String name;
                if (nodeDetails.isLastLayer()) {
                    name = "NoDeps" + (item + 1);
                } else {
                    name = "Impl" + (layer) + "_" + (item + 1);
                }
                javaClass = component.addClass(className + name);
            }
            if (layer == implLayer) {
                for (Project depProject : project.getDependencies()) {
                    javaClass.uses(depProject.component(JvmLibrary.class).getApi());
                }
                for (PublishedJvmLibrary library : project.getExternalDependencies()) {
                    javaClass.uses(library.getApi());
                }
                implClass.accept(javaClass);
            }
            for (JavaClass dep : dependencies) {
                javaClass.uses(dep);
            }
            return javaClass;
        });
    }
}
