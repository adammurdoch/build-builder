package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.Component;
import org.gradle.builds.model.HttpServer;
import org.gradle.builds.model.Project;

public class HttpServerModelAssembler extends AbstractModelAssembler {
    @Override
    public void apply(Class<? extends Component> component, Project project) {
    }

    @Override
    protected void populate(Settings settings, Project project) {
        HttpServer component = project.component(HttpServer.class);
        if (component != null) {
            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("application");
            buildScript.property("mainClassName", "org.gradle.example.http.Main");
            buildScript.statement("def publishTasks = { subprojects.collect { p -> p.tasks.getByName('publish') } }");
            buildScript.statement("installDist.dependsOn publishTasks");
            buildScript.statement("run.dependsOn publishTasks");
        }
    }
}
