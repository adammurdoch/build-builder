package org.gradle.builds.model

class KotlinClass(name: String) : JvmClass<KotlinClass>(name) {
    override fun getApi(): KotlinClass {
        return this
    }
}