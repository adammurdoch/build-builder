package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class CppModelAssembler extends AbstractModelAssembler {
    private final int appImplHeaders;
    private final int appPrivateHeaders;
    private final int libPublicHeaders;
    private final int libImplHeaders;
    private final int libPrivateHeaders;

    public CppModelAssembler(int headers) {
        appImplHeaders = (headers - 1) / 2;
        appPrivateHeaders = headers - appImplHeaders - 2;
        libPublicHeaders = (headers - 3) / 3;
        libImplHeaders = (headers - libPublicHeaders - 2) / 2;
        libPrivateHeaders = headers - libPublicHeaders - libImplHeaders - 3;
    }

    @Override
    protected void rootProject(Project rootProject) {
        ProjectScriptBlock allProjects = rootProject.getBuildScript().allProjects();
        allProjects.requirePlugin("xcode");
        allProjects.requirePlugin("maven-publish");
        allProjects.property("group", "test");
        allProjects.property("version", "1.2");
        allProjects.block("publishing").block("repositories").block("maven").property("url", new Scope.Code("rootProject.file('repo')"));
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

            for (int i = 0; i < libPublicHeaders; i++) {
                CppHeaderFile headerFile = lib.addPublicHeaderFile(project.getFileNameFor() + "_defs" + (i + 1) + ".h");
                apiHeader.includeHeader(headerFile);
            }

            CppHeaderFile implHeader = lib.addImplementationHeaderFile(project.getFileNameFor() + "_impl.h");
            implHeader.includeHeader(apiHeader);
            for (int i = 0; i < libImplHeaders; i++) {
                CppHeaderFile headerFile = lib.addImplementationHeaderFile(project.getFileNameFor() + "_impl_defs" + (i + 1) + ".h");
                implHeader.includeHeader(headerFile);
            }

            CppHeaderFile privateHeader = lib.addPrivateHeaderFile(project.getFileNameFor() + "_private.h");
            for (int i = 0; i < libPrivateHeaders; i++) {
                CppHeaderFile headerFile = lib.addPrivateHeaderFile(project.getFileNameFor() + "_private_defs" + (i + 1) + ".h");
                privateHeader.includeHeader(headerFile);
            }

            CppSourceFile apiSourceFile = lib.addSourceFile(project.getFileNameFor() + ".cpp");
            apiSourceFile.includeHeader(implHeader);
            apiSourceFile.includeHeader(privateHeader);
            apiSourceFile.addClass(apiClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("cpp-library");
            addPublishing(project, lib, project.getBuildScript());
            addDependencies(project, lib, buildScript);

            addApiHeaders(lib, apiHeader);
            addSource(project, lib, apiClass, apiSourceFile, implHeader, privateHeader);
        } else if (project.component(CppApplication.class) != null) {
            CppApplication app = project.component(CppApplication.class);

            CppClass appClass = new CppClass(project.getTypeNameFor());

            CppHeaderFile implHeader = app.addImplementationHeaderFile(project.getFileNameFor() + ".h");
            implHeader.addClass(appClass);
            for (int i = 0; i < appImplHeaders; i++) {
                CppHeaderFile headerFile = app.addImplementationHeaderFile(project.getFileNameFor() + "_defs" + (i + 1) + ".h");
                implHeader.includeHeader(headerFile);
            }

            CppHeaderFile privateHeader = app.addPrivateHeaderFile(project.getFileNameFor() + "_private.h");
            for (int i = 0; i < appPrivateHeaders; i++) {
                CppHeaderFile headerFile = app.addPrivateHeaderFile(project.getFileNameFor() + "_private_defs" + (i + 1) + ".h");
                privateHeader.includeHeader(headerFile);
            }

            CppSourceFile mainSourceFile = app.addSourceFile(project.getFileNameFor() + ".cpp");
            mainSourceFile.includeHeader(implHeader);
            mainSourceFile.includeHeader(privateHeader);
            mainSourceFile.addMainFunction(appClass);
            mainSourceFile.addClass(appClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("cpp-executable");
            addDependencies(project, app, buildScript);

            addSource(project, app, appClass, mainSourceFile, implHeader, privateHeader);
        }
    }

    private void addApiHeaders(CppLibrary lib, CppHeaderFile header) {
        for (Dependency<CppLibraryApi> dependency : lib.getReferencedLibraries()) {
            if (dependency.isApi()) {
                header.includeHeader(dependency.getTarget().getApiHeader());
            }
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
                buildScript.block("publishing").block("repositories").block("maven").property("url",
                        new Scope.Code("uri('" + project.getPublicationTarget().getHttpRepository().getRootDir().toUri() + "')"));
            }
        } else {
            project.export(new LocalLibrary<>(project, null, library.getApi()));
        }
    }

    private void addSource(Project project, HasCppSource component, CppClass entryPoint, CppSourceFile entryPointSourceFile, CppHeaderFile implHeader,
                           CppHeaderFile privateHeader) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers() - 2);
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
                cppSourceFile.includeHeader(privateHeader);
                cppSourceFile.includeHeader(implHeader);
                cppSourceFile.addClass(cppClass);
            }
            if (layer == implLayer) {
                addReferences(component, cppClass, cppSourceFile);
            }
            for (Dependency<CppClass> dep : dependencies) {
                cppClass.uses(dep);
            }
            return cppClass;
        });
    }

    private void addReferences(HasCppSource component, CppClass cppClass, CppSourceFile sourceFile) {
        for (Dependency<CppLibraryApi> dependency : component.getReferencedLibraries()) {
            CppLibraryApi api = dependency.getTarget();
            cppClass.uses(dependency.withTarget(api.getApiClass()));
            sourceFile.includeHeader(api.getApiHeader());
        }
    }

    private void addDependencies(Project project, HasCppSource component, BuildScript buildScript) {
        for (Dependency<Library<? extends CppLibraryApi>> dependency : project.getRequiredLibraries(CppLibraryApi.class)) {
            Library<? extends CppLibraryApi> library = dependency.getTarget();
            if (dependency.isApi() && component instanceof CppLibrary) {
                buildScript.dependsOn("api", library.getDependency());
            } else {
                buildScript.dependsOn("implementation", library.getDependency());
            }
            component.uses(dependency.withTarget(library.getApi()));
        }
    }
}
