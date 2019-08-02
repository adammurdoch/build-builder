package org.gradle.builds.model;

import org.gradle.builds.assemblers.ComposableProjectInitializer;
import org.gradle.builds.assemblers.Settings;

import java.nio.file.Path;

public interface BuildSettingsBuilder {
    Path getRootDir();

    void setDisplayName(String displayName);

    String getDisplayName();

    void setRootProjectName(String rootProjectName);

    String getRootProjectName();

    void setSettings(Settings settings);

    Settings getSettings();

    ComposableProjectInitializer getProjectInitializer();

    void setTypeNamePrefix(String typeNamePrefix);

    String getTypeNamePrefix();

    void setVersion(String version);

    String getVersion();

    void publishAs(PublicationTarget publicationTarget);

    void sourceDependency(BuildSettingsBuilder childBuild);

    void dependsOn(BuildSettingsBuilder childBuild);

    void includeBuild(BuildSettingsBuilder childBuild);
}
