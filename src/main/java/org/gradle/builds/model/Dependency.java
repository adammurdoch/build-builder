package org.gradle.builds.model;

public class Dependency<T> {
    private final T target;
    private boolean api;

    private Dependency(T target, boolean api) {
        this.target = target;
        this.api = api;
    }

    public static <T> Dependency<T> api(T target) {
        return new Dependency<>(target, true);
    }

    public static <T> Dependency<T> implementation(T target) {
        return new Dependency<>(target, false);
    }

    @Override
    public String toString() {
        return "{" + (api ? "api " : "") + target + "}";
    }

    public T getTarget() {
        return target;
    }

    /**
     * Should this dependency be exported as an API dependency?
     */
    public boolean isApi() {
        return api;
    }

    public <S> Dependency<S> withTarget(S target) {
        return new Dependency<>(target, api);
    }

    public Dependency<T> asImplementation() {
        if (!api) {
            return this;
        }
        return new Dependency<>(target, false);
    }
}
