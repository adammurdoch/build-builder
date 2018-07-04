package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.nio.file.Path;

public class HttpServerModelAssembler extends ComponentSpecificProjectConfigurer<HttpServerImplementation> {
    public HttpServerModelAssembler() {
        super(HttpServerImplementation.class);
    }

    @Override
    public void configure(Settings settings, Project project, HttpServerImplementation component) {
        BuildScript buildScript = project.getBuildScript();
        buildScript.requirePlugin("application");
        buildScript.property("mainClassName", "org.gradle.example.http.RepoMain");
        for (int i = 0; i < component.getSourceBuilds().size(); i++) {
            Path path = component.getSourceBuilds().get(i);
            String publishTaskName = "publishV" + (i + 1);
            ScriptBlock taskBlock = buildScript.block("task " + publishTaskName + "(type: GradleBuild)");
            taskBlock.property("dir", new Scope.Code("file('" + path.toUri() + "')"));
            taskBlock.property("tasks", new Scope.Code("['publish']"));
        }
        buildScript.statement("installDist.dependsOn tasks.withType(GradleBuild)");
        buildScript.statement("run.dependsOn tasks.withType(GradleBuild)");
    }
}
