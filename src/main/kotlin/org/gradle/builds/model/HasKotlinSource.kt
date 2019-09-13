package org.gradle.builds.model

open class HasKotlinSource: HasClasses<KotlinClass, KotlinClass, KotlinLibraryApi>() {
    override fun addClass(name: String): KotlinClass {
        return addSourceFile(KotlinClass(name))
    }
}