package org.gradle.builds.model;

public interface Library<T> extends Component {
    T getApi();
}
