package org.gradle.builds.model

open class HasKotlinSource: HasSource<KotlinClass, KotlinLibraryApi>() {
    fun addClass(name: String): KotlinClass {
        return addSourceFile(KotlinClass(name))
    }
}