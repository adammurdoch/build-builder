package org.gradle.builds.model

class KotlinLibrary: HasKotlinSource(), HasApi {
    override val api: LibraryApi = KotlinLibraryApi()
}