package org.gradle.builds.model

open class HasKotlinSource: HasSource<KotlinClass, Unit>() {
    fun addClass(name: String): KotlinClass {
        return addSourceFile(KotlinClass(name))
    }
}