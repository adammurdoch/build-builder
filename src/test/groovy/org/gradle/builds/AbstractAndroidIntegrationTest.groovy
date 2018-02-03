package org.gradle.builds

abstract class AbstractAndroidIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.1"
    }
}
