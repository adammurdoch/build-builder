package org.gradle.builds.model

open class JvmClass<REF>(name: String) : SourceClass<REF>(name) {
    val packageName: String
        get() {
            val name = name
            val pos = name.lastIndexOf(".")
            return name.substring(0, pos)
        }

    val simpleName: String
        get() {
            val name = name
            val pos = name.lastIndexOf(".")
            return name.substring(pos + 1)
        }
}