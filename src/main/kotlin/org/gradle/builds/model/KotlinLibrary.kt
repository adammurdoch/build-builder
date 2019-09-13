package org.gradle.builds.model

class KotlinLibrary(project: Project): HasKotlinSource(), HasApi {
    val apiClass = addClass("${project.qualifiedNamespaceFor}.${project.typeNameFor}")

    override val api: LibraryApi = KotlinLibraryApi(apiClass)
}