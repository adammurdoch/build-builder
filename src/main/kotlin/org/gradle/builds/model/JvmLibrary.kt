package org.gradle.builds.model

interface JvmLibrary : HasApi<JavaClassApi> {
    override val api: JvmLibraryApi
}
