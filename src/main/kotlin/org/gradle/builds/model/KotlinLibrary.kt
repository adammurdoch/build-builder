package org.gradle.builds.model

class KotlinLibrary(project: Project): HasKotlinSource(), HasApi<KotlinClass> {
    val apiClass = addClass("${project.qualifiedNamespaceFor}.${project.typeNameFor}")

    override val api = KotlinLibraryApi(apiClass)
}