package org.gradle.builds.model

abstract class HasClasses<REF, T: SourceClass<REF>, L: LibraryApi<REF>>: HasSource<T, L>() {
    abstract fun addClass(name: String): T
}