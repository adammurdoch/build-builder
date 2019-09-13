package org.gradle.builds.model

interface LibraryApi<REF> {
    val apiClasses: List<REF>
}