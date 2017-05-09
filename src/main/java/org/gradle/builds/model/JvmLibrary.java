package org.gradle.builds.model;

import java.util.Set;

public interface JvmLibrary extends Component {
    Set<JavaClassApi> getApi();
}
