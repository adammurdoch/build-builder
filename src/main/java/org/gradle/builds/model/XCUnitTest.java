package org.gradle.builds.model;

public class XCUnitTest implements ClassRole {
    private final SwiftClass classUnderTest;

    public XCUnitTest(SwiftClass classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    public SwiftClass getClassUnderTest() {
        return classUnderTest;
    }
}
