package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class CppModelAssembler extends LanguageSpecificProjectConfigurer<CppApplication, CppLibrary> {
    private final int appImplHeaders;
    private final int appPrivateHeaders;
    private final int libPublicHeaders;
    private final int libImplHeaders;
    private final int libPrivateHeaders;
    private final MacroIncludes macroIncludes;
    private final boolean boost;

    public CppModelAssembler(int headers, MacroIncludes macroIncludes, boolean boost) {
        super(CppApplication.class, CppLibrary.class);
        appImplHeaders = (headers - 1) / 2;
        appPrivateHeaders = headers - appImplHeaders - 2;
        libPublicHeaders = (headers - 3) / 3;
        libImplHeaders = (headers - libPublicHeaders - 2) / 2;
        libPrivateHeaders = headers - libPublicHeaders - libImplHeaders - 3;
        this.macroIncludes = macroIncludes;
        this.boost = boost;
    }

    @Override
    protected void rootProject(Settings settings, Project rootProject) {
        rootProject.getBuildScript().requirePlugin("swiftpm-export", "4.6");

        addIdePlugins(rootProject);
        BlockWithProjectTarget allProjects = rootProject.getBuildScript().allProjects();
        allProjects.requirePlugin("maven-publish");
        allProjects.property("group", "test");
        allProjects.property("version", rootProject.getVersion());
        allProjects.block("publishing").block("repositories").block("maven").property("url", new Scope.Code("rootProject.file('repo')"));
    }

    @Override
    protected void application(Settings settings, Project project, CppApplication app) {
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
        buildScript.requirePlugin("cpp-executable", "4.2", "4.4");
        buildScript.requirePlugin("cpp-application", "4.5");
        buildScript.requirePlugin("cpp-unit-test");
        addDependencies(project, app, buildScript);
        maybeAddBoost(privateHeader, buildScript);

        addApiHeaders(app, implHeader);
        addSource(project, app, appClass, mainSourceFile, implHeader, privateHeader);
        addTests(project, app, implHeader);
        app.setMacroIncludes(macroIncludes);
    }

    @Override
    protected void library(Settings settings, Project project, CppLibrary lib) {
        CppClass apiClass = lib.getApiClass();

        CppHeaderFile apiHeader = lib.getApiHeader();
        apiHeader.addClass(apiClass);

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
        buildScript.requirePlugin("cpp-unit-test");
        addPublishing(project, project.getBuildScript());
        addDependencies(project, lib, buildScript);
        maybeAddBoost(privateHeader, buildScript);

        addApiHeaders(lib, apiHeader);
        addSource(project, lib, apiClass, apiSourceFile, implHeader, privateHeader);
        addTests(project, lib, implHeader);
        lib.setMacroIncludes(macroIncludes);
    }

    private void maybeAddBoost(CppHeaderFile privateHeader, BuildScript buildScript) {
        if (boost) {
            privateHeader.includeSystemHeader("boost/asio.hpp");
        }
        if (boost) {
            buildScript.block("tasks.withType(AbstractLinkTask)").statement("linkerArgs.add('-lboost_system')");
        }
    }

    private void addApiHeaders(HasCppSource component, CppHeaderFile header) {
        for (Dependency<CppLibraryApi> dependency : component.getReferencedLibraries()) {
            if (dependency.isApi()) {
                header.includeHeader(dependency.getTarget().getApiHeader());
            }
        }
    }

    private void addPublishing(Project project, BuildScript buildScript) {
        if (project.getPublicationTarget() != null) {
            String group = "org.gradle.example";
            String version = project.getVersion();
            buildScript.property("group", group);
            buildScript.property("version", version);
            if (project.getPublicationTarget().getHttpRepository() != null) {
                buildScript.requirePlugin("maven-publish");
                buildScript.block("publishing").block("repositories").block("maven").property("url",
                        new Scope.Code("uri('" + project.getPublicationTarget().getHttpRepository().getRootDir().toUri() + "')"));
            }
        }
    }

    private void addSource(Project project, HasCppSource component, CppClass entryPoint, CppSourceFile entryPointSourceFile, CppHeaderFile implHeader,
                           CppHeaderFile privateHeader) {
        project.getClassGraph().visit((Graph.Visitor<CppClass>) (nodeDetails, dependencies) -> {
            CppClass cppClass;
            CppSourceFile cppSourceFile;
            int layer = nodeDetails.getLayer();
            if (layer == 0) {
                // Entry points should be encoded in the graph
                cppClass = entryPoint;
                cppSourceFile = entryPointSourceFile;
                // Incoming API/implementation dependencies should be encoded in the graph
                addIncomingApiDependencies(component, cppClass, cppSourceFile);
                if (nodeDetails.isReceiveIncoming()) {
                    addIncomingImplementationDependencies(component, cppClass, cppSourceFile);
                }
                for (Dependency<CppClass> dep : dependencies) {
                    // Forcing to implementation should be encoded in the graph
                    cppClass.uses(dep.asImplementation());
                }
            } else {
                cppClass = new CppClass(entryPoint.getName() + "Impl" + nodeDetails.getNameSuffix());
                implHeader.addClass(cppClass);
                cppSourceFile = component.addSourceFile(cppClass.getName().toLowerCase() + ".cpp");
                cppSourceFile.includeHeader(privateHeader);
                cppSourceFile.includeHeader(implHeader);
                cppSourceFile.addClass(cppClass);
                if (nodeDetails.isReceiveIncoming()) {
                    // Incoming API/implementation dependencies should be encoded in the graph
                    // Forcing to implementation should be encoded in the graph
                    addIncomingDependenciesAsImplementation(component, cppClass, cppSourceFile);
                }
                for (Dependency<CppClass> dep : dependencies) {
                    cppClass.uses(dep);
                }
            }
            return cppClass;
        });
    }

    private void addTests(Project project, HasCppSource component, CppHeaderFile header) {
        CppHeaderFile testHeaderFile = component.addTestHeaderFile(project.getFileNameFor() + "_test.h");
        testHeaderFile.includeHeader(header);

        CppSourceFile testMain = component.addTestFile(new CppSourceFile("test_main.cpp"));
        testMain.includeHeader(testHeaderFile);

        for (CppSourceFile cppSourceFile : component.getSourceFiles()) {
            for (CppClass cppClass : cppSourceFile.getClasses()) {
                CppSourceFile sourceFile = new CppSourceFile(cppClass.getName().toLowerCase() + "_test.cpp");
                component.addTestFile(sourceFile);
                sourceFile.includeHeader(testHeaderFile);
                CppClass testClass = new CppClass(cppClass.getName() + "Test");
                testHeaderFile.addClass(testClass);
                sourceFile.addClass(testClass);
                testMain.addMainFunction(testClass);
            }
        }
    }

    private void addIncomingApiDependencies(HasCppSource component, CppClass cppClass, CppSourceFile sourceFile) {
        for (Dependency<CppLibraryApi> dependency : component.getReferencedLibraries()) {
            if (dependency.isApi()) {
                CppLibraryApi api = dependency.getTarget();
                cppClass.uses(dependency.withTarget(api.getApiClass()));
                sourceFile.includeHeader(api.getApiHeader());
            }
        }
    }

    private void addIncomingImplementationDependencies(HasCppSource component, CppClass cppClass, CppSourceFile sourceFile) {
        for (Dependency<CppLibraryApi> dependency : component.getReferencedLibraries()) {
            if (!dependency.isApi()) {
                CppLibraryApi api = dependency.getTarget();
                cppClass.uses(dependency.withTarget(api.getApiClass()));
                sourceFile.includeHeader(api.getApiHeader());
            }
        }
    }

    private void addIncomingDependenciesAsImplementation(HasCppSource component, CppClass cppClass, CppSourceFile sourceFile) {
        for (Dependency<CppLibraryApi> dependency : component.getReferencedLibraries()) {
            CppLibraryApi api = dependency.getTarget();
            cppClass.uses(Dependency.implementation(api.getApiClass()));
            sourceFile.includeHeader(api.getApiHeader());
        }
    }

    private void addDependencies(Project project, HasCppSource component, BuildScript buildScript) {
        for (Dependency<Library<? extends CppLibraryApi>> dependency : project.requiredLibraries(CppLibraryApi.class)) {
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
