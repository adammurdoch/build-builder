package org.gradle.builds.model

interface JvmLibrary : HasApi {
    override val api: JvmLibraryApi
}
