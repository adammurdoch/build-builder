package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class CppModelAssembler extends AbstractModelAssembler {
    @Override
    protected void rootProject(Project rootProject) {
        ProjectScriptBlock allProjects = rootProject.getBuildScript().allProjects();
        allProjects.requirePlugin("xcode");
        allProjects.requirePlugin("maven-publish");
        allProjects.property("group", "test");
        allProjects.property("version", "1.2");
        allProjects.block("publishing")
                .block("repositories")
                .block("maven")
                .property("url", new Scope.Code("rootProject.file('repo')"));
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(CppLibrary.class) != null) {
            CppLibrary lib = project.component(CppLibrary.class);

            CppClass apiClass = new CppClass(classNameFor(project));
            lib.setApiClass(apiClass);

            CppHeaderFile apiHeader = lib.addPublicHeaderFile(fileNameFor(project) + ".h");
            apiHeader.addClass(apiClass);
            lib.setApiHeader(apiHeader);

            CppHeaderFile implHeader = lib.addImplementationHeaderFile(fileNameFor(project) + "_impl.h");
            implHeader.addHeader(apiHeader);

            CppSourceFile apiSourceFile = lib.addSourceFile(fileNameFor(project) + ".cpp");
            apiSourceFile.addClass(apiClass);
            apiSourceFile.addHeader(implHeader);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("cpp-library");
            addPublishing(project, lib, project.getBuildScript());
            addDependencies(project, lib, buildScript);

            addSource(project, lib, apiClass, apiSourceFile, implHeader);
        } else if (project.component(CppApplication.class) != null) {
            CppApplication app = project.component(CppApplication.class);

            CppClass appClass = new CppClass(classNameFor(project));

            CppHeaderFile headerFile = app.addImplementationHeaderFile(fileNameFor(project) + ".h");
            headerFile.addClass(appClass);

            CppSourceFile mainSourceFile = app.addSourceFile(fileNameFor(project) + ".cpp");
            mainSourceFile.addMainFunction(appClass);
            mainSourceFile.addHeader(headerFile);
            mainSourceFile.addClass(appClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("cpp-executable");
            addDependencies(project, app, buildScript);

            addSource(project, app, appClass, mainSourceFile, headerFile);
        }
    }

    private void addPublishing(Project project, CppLibrary library, BuildScript buildScript) {
        if (project.getPublicationTarget() != null) {
            String group = "org.gradle.example";
            String module = project.getName();
            String version = "1.2";
            project.export(new LocalLibrary<>(project, new ExternalDependencyDeclaration(group, module, version), library.getApi()));
            buildScript.property("group", group);
            buildScript.property("version", version);
            if (project.getPublicationTarget().getHttpRepository() != null) {
                buildScript.requirePlugin("maven-publish");
                buildScript.block("publishing").block("repositories").block("maven").property("url", new Scope.Code("uri('" + project.getPublicationTarget().getHttpRepository().getRootDir().toUri() + "')"));
            }
        } else {
            project.export(new LocalLibrary<>(project, null, library.getApi()));
        }
    }

    private void addSource(Project project, HasCppSource component, CppClass entryPoint, CppSourceFile entryPointSourceFile, CppHeaderFile implHeader) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers().size() - 2);
        project.getClassGraph().visit((Graph.Visitor<CppClass>) (nodeDetails, dependencies) -> {
            CppClass cppClass;
            CppSourceFile cppSourceFile;
            int layer = nodeDetails.getLayer();
            int item = nodeDetails.getItem();
            if (layer == 0) {
                cppClass = entryPoint;
                cppSourceFile = entryPointSourceFile;
            } else {
                String name;
                if (nodeDetails.isLastLayer()) {
                    name = "NoDeps" + (item + 1);
                } else {
                    name = "Impl" + (layer) + "_" + (item + 1);
                }
                cppClass = new CppClass(entryPoint.getName() + name);
                implHeader.addClass(cppClass);
                cppSourceFile = component.addSourceFile(fileNameFor(project) + "_" + name.toLowerCase() + ".cpp");
                cppSourceFile.addClass(cppClass);
                cppSourceFile.addHeader(implHeader);
            }
            if (layer == implLayer) {
                addReferences(component, cppClass, cppSourceFile);
            }
            for (CppClass dep : dependencies) {
                cppClass.uses(dep);
            }
            return cppClass;
        });
    }

    private void addReferences(HasCppSource component, CppClass cppClass, CppSourceFile sourceFile) {
        for (CppLibraryApi library : component.getReferencedLibraries()) {
            cppClass.uses(library.getApiClass());
            sourceFile.addHeader(library.getApiHeader());
        }
    }

    private void addDependencies(Project project, HasCppSource component,  BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependsOn("implementation", new ProjectDependencyDeclaration(dep.getPath()));
            for (LocalLibrary<? extends CppLibraryApi> library : dep.getExportedLibraries(CppLibraryApi.class)) {
                component.uses(library.getApi());
            }
        }
        for (PublishedLibrary<? extends CppLibraryApi> library : project.getExternalDependencies(CppLibraryApi.class)) {
            buildScript.dependsOn("implementation", library.getDependency());
            component.uses(library.getApi());
        }
    }
}
