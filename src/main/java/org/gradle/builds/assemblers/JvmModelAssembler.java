package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.util.Collections;
import java.util.function.Consumer;

public abstract class JvmModelAssembler<A extends Component, L extends Component> extends LanguageSpecificProjectConfigurer<A, L> {
    private static final JavaLibraryApi slf4jApi = new JavaLibraryApi("slf4j", Collections.singletonList(JavaClassApi.method("org.slf4j.LoggerFactory", "getLogger(\"abc\")")));
    static final PublishedLibrary<JavaLibraryApi> slfj4 = new PublishedLibrary<>("slf4j", new ExternalDependencyDeclaration("org.slf4j:slf4j-api:1.7.25"), slf4jApi);
    static final PublishedLibrary<JavaLibraryApi> slfj4Simple = new PublishedLibrary<>("slf4j-simple", new ExternalDependencyDeclaration("org.slf4j:slf4j-simple:1.7.25"), new JavaLibraryApi("slaf4-simple", Collections.emptyList()));

    public JvmModelAssembler(Class<A> applicationType, Class<L> libraryType) {
        super(applicationType, libraryType);
    }

    @Override
    protected void rootProject(Settings settings, Project rootProject) {
        BlockWithProjectTarget allProjects = rootProject.getBuildScript().allProjects();
        allProjects.jcenter();
        addIdePlugins(rootProject);
    }

    protected void addTests(Project project, HasJavaSource<?> application) {
        for (JavaClass javaClass : application.getSourceFiles()) {
            JavaClass testClass = application.addTest(javaClass.getName() + "Test");
            testClass.addRole(new UnitTest(javaClass));
        }
    }

    protected static <REF, T extends SourceClass<REF>, L extends LibraryApi<REF>> void addSource(Project project, HasClasses<REF, T, L> component, T entryPoint, Consumer<T> implClass) {
        String className = project.getQualifiedNamespaceFor() + "." + project.getTypeNameFor();
        project.getClassGraph().visit((Graph.Visitor<T>) (nodeDetails, dependencies) -> {
            T classNode;
            int layer = nodeDetails.getLayer();
            if (layer == 0) {
                classNode = entryPoint;
            } else {
                classNode = component.addClass(className + "Impl" + nodeDetails.getNameSuffix());
            }
            if (nodeDetails.isReceiveIncoming()) {
                for (Dependency<L> dependency : component.getReferencedLibraries()) {
                    for (REF incomingApi : dependency.getTarget().getApiClasses()) {
                        classNode.uses(dependency.withTarget(incomingApi));
                    }
                }
                implClass.accept(classNode);
            }
            for (Dependency<T> dep : dependencies) {
                classNode.uses(dep.withTarget(dep.getTarget().getApi()));
            }
            return classNode;
        });
    }
}
