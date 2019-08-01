package org.gradle.builds.assemblers

import org.gradle.builds.model.KotlinApplication
import org.gradle.builds.model.KotlinLibrary
import org.gradle.builds.model.Project

class KotlinModelAssembler: LanguageSpecificProjectConfigurer<KotlinApplication, KotlinLibrary>(KotlinApplication::class.java, KotlinLibrary::class.java) {
    override fun rootProject(settings: Settings, project: Project) {
    }

    override fun application(settings: Settings, project: Project, application: KotlinApplication) {
    }

    override fun library(settings: Settings, project: Project, library: KotlinLibrary) {
    }
}