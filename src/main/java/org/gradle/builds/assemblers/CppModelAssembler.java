package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.util.HashSet;
import java.util.Set;

public class CppModelAssembler extends AbstractModelAssembler {
    @Override
    public void apply(Class<? extends Component> component, Project project) {
        if (component.equals(NativeLibrary.class) || component.equals(Library.class)) {
            project.addComponent(new NativeLibrary());
        } else if (component.equals(NativeApplication.class) || component.equals(Application.class)) {
            project.addComponent(new NativeApplication());
        }
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(NativeLibrary.class) != null) {
            NativeLibrary lib = project.component(NativeLibrary.class);

            CppClass apiClass = new CppClass(classNameFor(project));
            lib.setApiClass(apiClass);

            CppHeaderFile apiHeader = lib.addHeaderFile(fileNameFor(project) + ".h");
            apiHeader.addClass(apiClass);
            lib.setApiHeader(apiHeader);

            CppHeaderFile implHeader = lib.addHeaderFile(fileNameFor(project) + "_impl.h");
            implHeader.addHeader(apiHeader);

            CppSourceFile apiSourceFile = lib.addSourceFile(fileNameFor(project) + ".cpp");
            apiSourceFile.addClass(apiClass);
            apiSourceFile.addHeader(implHeader);

            addSource(project, lib, apiClass, apiSourceFile, implHeader);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("native-component");
            buildScript.requirePlugin("cpp-lang");
            SoftwareModelDeclaration componentDeclaration = buildScript.componentDeclaration("main", "NativeLibrarySpec");
            addDependencies(project, componentDeclaration);
        } else if (project.component(NativeApplication.class) != null) {
            NativeApplication app = project.component(NativeApplication.class);

            CppClass appClass = new CppClass(classNameFor(project));

            CppHeaderFile headerFile = app.addHeaderFile(fileNameFor(project) + ".h");
            headerFile.addClass(appClass);

            CppSourceFile mainSourceFile = app.addSourceFile(fileNameFor(project) + ".cpp");
            mainSourceFile.addMainFunction(appClass);
            mainSourceFile.addHeader(headerFile);
            mainSourceFile.addClass(appClass);

            addSource(project, app, appClass, mainSourceFile, headerFile);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("native-component");
            buildScript.requirePlugin("cpp-lang");
            SoftwareModelDeclaration componentDeclaration = buildScript.componentDeclaration("main", "NativeExecutableSpec");
            addDependencies(project, componentDeclaration);
        }
    }

    private void addSource(Project project, HasNativeSource component, CppClass apiClass, CppSourceFile apiSourceFile, CppHeaderFile implHeader) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers().size() - 2);
        project.getClassGraph().visit((Graph.Visitor<CppClass>) (layer, item, lastLayer, dependencies) -> {
            CppClass cppClass;
            CppSourceFile cppSourceFile;
            if (layer == 0) {
                cppClass = apiClass;
                cppSourceFile = apiSourceFile;
            } else {
                String name;
                if (lastLayer) {
                    name = "NoDeps" + (item + 1);
                } else {
                    name = "Impl" + (layer) + "_" + (item + 1);
                }
                cppClass = new CppClass(apiClass.getName() + name);
                implHeader.addClass(cppClass);
                cppSourceFile = component.addSourceFile(fileNameFor(project) + "_" + name.toLowerCase() + ".cpp");
                cppSourceFile.addClass(cppClass);
                cppSourceFile.addHeader(implHeader);
            }
            if (layer == implLayer) {
                addReferences(project, cppClass);
                addLibHeaders(project, cppSourceFile);
            }
            for (CppClass dep : dependencies) {
                cppClass.uses(dep);
            }
            return cppClass;
        });
    }

    private void addLibHeaders(Project project, CppSourceFile sourceFile) {
        for (Project dep : project.getDependencies()) {
            sourceFile.addHeader(dep.component(NativeLibrary.class).getApiHeader());
        }
    }

    private void addReferences(Project project, CppClass cppClass) {
        for (Project dep : project.getDependencies()) {
            cppClass.uses(dep.component(NativeLibrary.class).getApiClass());
        }
    }

    private void addDependencies(Project project, SoftwareModelDeclaration componentDeclaration) {
        // Need to include transitive dependencies
        Set<Project> seen = new HashSet<>();
        for (Project dep : project.getDependencies()) {
            addDependencies(componentDeclaration, dep, seen);
        }
    }

    private void addDependencies(SoftwareModelDeclaration componentDeclaration, Project project, Set<Project> seen) {
        if (!seen.add(project)) {
            return;
        }
        componentDeclaration.dependsOn(project.getPath());
        for (Project dep : project.getDependencies()) {
            addDependencies(componentDeclaration, dep, seen);
        }
    }
}
