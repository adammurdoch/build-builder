package org.gradle.builds.model


class GradlePluginComponent: HasJavaSource<JvmLibraryApi>() {
    var id: String? = null
    var implClass: JavaClass? = null
}