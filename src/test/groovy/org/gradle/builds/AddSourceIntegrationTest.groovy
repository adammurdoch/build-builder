package org.gradle.builds

class AddSourceIntegrationTest extends AbstractIntegrationTest {
    def "inspects and adds source to a skeleton build with a mixture of JVM project types"() {
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
                    classpath 'com.android.tools.build:gradle:2.3.1'
                }
            }
            project(':javaApp') {
                apply plugin: 'java'
                apply plugin: 'application'
                mainClassName = 'org.gradle.example.app.Main'
                dependencies {
                    compile project(':javaLib')
                }
            }
            project(':javaLib') {
                apply plugin: 'java'
                dependencies {
                    compile project(':util:javaLib')
                }
            }
            project(':util:javaLib') {
                apply plugin: 'java'
            }
            project(':androidApp') {
                apply plugin: 'com.android.application'
                dependencies {
                    compile project(':androidLib')
                    compile project(':javaLib')
                }
            }
            project(':androidLib') {
                apply plugin: 'com.android.library'
                dependencies {
                    compile project(':util:androidLib')
                    compile project(':util:javaLib')
                }
            }
            project(':util:androidLib') {
                apply plugin: 'com.android.library'
            }
            allprojects {
                repositories {
                    jcenter()
                }
                plugins.withId('java') {
                    dependencies {
                        testCompile 'junit:junit:4.12'
                        sourceCompatibility = 1.7
                    }
                }
                plugins.withId('com.android.application') {
                    android {
                        buildToolsVersion = '25.0.0'
                        compileSdkVersion = 25
                    }
                    dependencies {
                        testCompile 'junit:junit:4.12'
                    }
                }
                plugins.withId('com.android.library') {
                    android {
                        buildToolsVersion = '25.0.0'
                        compileSdkVersion = 25
                    }
                    dependencies {
                        testCompile 'junit:junit:4.12'
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

    def "inspects and adds source to a skeleton build with a mixture of native project types"() {
        file("settings.gradle") << """
            include 'app'
            include 'lib'
            include 'util:core1'
            include 'util:core2'
            include 'empty'
"""

        file("build.gradle") << """
            project(':app') {
                apply plugin: 'native-component'
                apply plugin: 'cpp-lang'
                model {
                    components {
                        app(NativeExecutableSpec)
                    }
                }
            }
            project(':lib') {
                apply plugin: 'native-component'
                apply plugin: 'cpp-lang'
                model {
                    components {
                        app(NativeLibrarySpec)
                    }
                }
            }
            project(':util:core1') {
                apply plugin: 'native-component'
                apply plugin: 'cpp-lang'
                model {
                    components {
                        app(NativeLibrarySpec)
                    }
                }
            }
            project(':util:core2') {
                apply plugin: 'native-component'
                apply plugin: 'cpp-lang'
                model {
                    components {
                        app(NativeLibrarySpec)
                    }
                }
            }
"""

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
