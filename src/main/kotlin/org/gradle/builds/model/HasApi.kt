package org.gradle.builds.model

interface HasApi<REF>: Component {
    val api: LibraryApi<REF>
}