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

            CppClass apiClass = new CppClass(project.getTypeNameFor());
            lib.setApiClass(apiClass);

            CppHeaderFile apiHeader = lib.addPublicHeaderFile(project.getFileNameFor() + ".h");
            apiHeader.addClass(apiClass);
            lib.setApiHeader(apiHeader);

            CppHeaderFile implHeader = lib.addImplementationHeaderFile(project.getFileNameFor() + "_impl.h");
            implHeader.addHeader(apiHeader);

            CppSourceFile apiSourceFile = lib.addSourceFile(project.getFileNameFor() + ".cpp");
            apiSourceFile.addClass(apiClass);
            apiSourceFile.addHeader(implHeader);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("cpp-library");
            addPublishing(project, lib, project.getBuildScript());
            addDependencies(project, lib, buildScript);

            addSource(project, lib, apiClass, apiSourceFile, implHeader);
        } else if (project.component(CppApplication.class) != null) {
            CppApplication app = project.component(CppApplication.class);

            CppClass appClass = new CppClass(project.getTypeNameFor());

            CppHeaderFile headerFile = app.addImplementationHeaderFile(project.getFileNameFor() + ".h");
            headerFile.addClass(appClass);

            CppSourceFile mainSourceFile = app.addSourceFile(project.getFileNameFor() + ".cpp");
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
            if (layer == 0) {
                cppClass = entryPoint;
                cppSourceFile = entryPointSourceFile;
            } else {
                cppClass = new CppClass(entryPoint.getName() + "Impl" + nodeDetails.getNameSuffix());
                implHeader.addClass(cppClass);
                cppSourceFile = component.addSourceFile(cppClass.getName().toLowerCase() + ".cpp");
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
        for (Library<? extends CppLibraryApi> library : project.getRequiredLibraries(CppLibraryApi.class)) {
            buildScript.dependsOn("implementation", library.getDependency());
            component.uses(library.getApi());
        }
    }
}
