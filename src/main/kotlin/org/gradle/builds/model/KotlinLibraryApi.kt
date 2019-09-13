package org.gradle.builds.model

class KotlinLibraryApi(val apiClass: KotlinClass) : LibraryApi {
    val apiClasses: List<KotlinClass>
        get() = listOf(apiClass)
}