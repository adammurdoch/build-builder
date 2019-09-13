package org.gradle.builds.model

class KotlinLibraryApi(val apiClass: KotlinClass) : LibraryApi<KotlinClass> {
    override val apiClasses: List<KotlinClass>
        get() = listOf(apiClass)
}