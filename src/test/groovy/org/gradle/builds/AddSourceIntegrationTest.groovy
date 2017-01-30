package org.gradle.builds

class AddSourceIntegrationTest extends AbstractIntegrationTest {
    def "inspects and adds source to a skeleton build with a mixture of project types"() {
        file("settings.gradle") << """
            include 'javaApp'
            include 'javaLib'
            include 'androidApp'
            include 'androidLib'
            include 'util:javaLib'
            include 'util:androidLib'
            include 'empty'
"""

        file("build.gradle") << """
            buildscript {
                repositories {
                    jcenter()
                }
                dependencies {
                    classpath 'com.android.tools.build:gradle:2.2.2'
                }
            }
            project(':javaApp') {
                apply plugin: 'java'
                apply plugin: 'application'
            }
            project(':javaLib') {
                apply plugin: 'java'
            }
            project(':util:javaLib') {
                apply plugin: 'java'
            }
            project(':androidApp') {
                apply plugin: 'com.android.application'
            }
            project(':androidLib') {
                apply plugin: 'com.android.library'
            }
            project(':util:androidLib') {
                apply plugin: 'com.android.library'
            }
            allprojects {
                plugins.withId('com.android.application') {
                    android {
                        buildToolsVersion = '25.0.0'
                        compileSdkVersion = 25
                    }
                }
                plugins.withId('com.android.library') {
                    android {
                        buildToolsVersion = '25.0.0'
                        compileSdkVersion = 25
                    }
                }
            }
"""
        file("local.properties") << "sdk.dir=/users/adam/Library/Android/sdk"
        writeAndroidManifest(file("androidApp/src/main/AndroidManifest.xml"), "org.gradle.example")
        writeAndroidManifest(file("androidLib/src/main/AndroidManifest.xml"), "org.gradle.example.lib")
        writeAndroidManifest(file("util/androidLib/src/main/AndroidManifest.xml"), "org.gradle.example.util.lib")

        when:
        new Main().run("add-source", "--dir", projectDir.absolutePath, "--source-files", "2")

        then:
        buildSucceeds("build")
    }

    private void writeAndroidManifest(File manifest, String packageName) {
        manifest.parentFile.mkdirs()
        manifest << """<manifest xmlns:android='http://schemas.android.com/apk/res/android'
                package='${packageName}'></manifest>"""
    }
}
