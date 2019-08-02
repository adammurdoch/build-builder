package org.gradle.builds.model

// TODO - make everything immutable
interface ConfiguredBuildScript: ConfiguredScope {
    val buildScriptBlock: BlockWithRepositories?

    val buildScriptClasspath: Set<ExternalDependencyDeclaration>

    val plugins: Set<BlockWithProjectTarget.Plugin>

    val allProjects: BlockWithProjectTarget?

    val repositories: Set<ScriptBlock>

    val dependencies: Map<String, Set<DependencyDeclaration>>

    val asBlock: Scope
}