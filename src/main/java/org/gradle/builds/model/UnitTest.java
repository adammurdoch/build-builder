package org.gradle.builds.model;

public class UnitTest implements ClassRole {
    private final JavaClass classUnderTest;

    public UnitTest(JavaClass classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    public JavaClass getClassUnderTest() {
        return classUnderTest;
    }
}
