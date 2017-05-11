package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.HttpServerImplementation;
import org.gradle.builds.model.Project;

public class HttpServerModelAssembler extends AbstractModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        HttpServerImplementation component = project.component(HttpServerImplementation.class);
        if (component != null) {
            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("application");
            buildScript.property("mainClassName", "org.gradle.example.http.RepoMain");
            buildScript.statement("def publishTasks = { subprojects.collect { p -> p.tasks.getByName('uploadArchives') } }");
            buildScript.statement("installDist.dependsOn publishTasks");
            buildScript.statement("run.dependsOn publishTasks");
        }
    }
}
