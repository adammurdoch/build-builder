package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class JavaModelAssembler extends ModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.getRole() == Project.Role.Library) {
            JavaLibrary library = project.addComponent(new JavaLibrary());
            JavaClass apiClass = library.addClass(javaPackageFor(project) + "." + classNameFor(project));
            library.setApiClass(apiClass);
            addSource(project, library, apiClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            addDependencies(project, buildScript);
        } else if (project.getRole() == Project.Role.Application) {
            JavaApplication application = project.addComponent(new JavaApplication());
            JavaClass mainClass = application.addClass(javaPackageFor(project) + "." + classNameFor(project));
            mainClass.addMainMethod();
            addSource(project, application, mainClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            buildScript.requirePlugin("application");
            addDependencies(project, buildScript);
            buildScript.property("mainClassName", mainClass.getName());
        }
    }

    private void addDependencies(Project project, BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependency("compile", dep.getPath());
        }
    }

    private void addSource(Project project, HasJavaSource library, JavaClass apiClass) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers().size() - 2);
        project.getClassGraph().visit((Graph.Visitor<JavaClass>) (layer, item, lastLayer, dependencies) -> {
            JavaClass javaClass;
            if (layer == 0) {
                javaClass = apiClass;
            } else {
                String name;
                if (lastLayer) {
                    name = "NoDeps" + (item + 1);
                } else {
                    name = "Impl" + (layer) + "_" + (item + 1);
                }
                javaClass = library.addClass(apiClass.getName() + name);
            }
            if (layer == implLayer) {
                for (Project depProject : project.getDependencies()) {
                    javaClass.uses(depProject.component(JvmLibrary.class).getApiClass());
                }
            }
            for (JavaClass dep : dependencies) {
                javaClass.uses(dep);
            }
            return javaClass;
        });
    }
}
