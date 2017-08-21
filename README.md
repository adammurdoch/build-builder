## build builder

Generates various builds that can be used for profiling and benchmarking Gradle.

Supported build types:

- Java application
- Android application
- C++ application (Gradle 4.2 and later)
- Swift application (Gradle 4.2 and later)

Generates one or more projects with source files. Can also be used to add source files to an existing skeleton build. 
The source files have dependencies between each other, as described below.

Generates JUnit tests for Java and Android projects, and on-device tests for Android projects.

Can optionally generate a composite build.

Can optionally generate a local HTTP repository and server.

Generates a [gradle-profiler](https://www.github.com/gradle/gradle-profiler) scenario file for the build.

### Command line usage

#### Installation

Run `./gradlew installDist` to build and install into `build/install/build-builder`.

#### Create a build

`build-builder java|cpp|swift|android [options]`

The `--dir` option specifies the directory to create the build init. Default is the current directory.

The `--projects` option specifies the number of projects. Default is 1.

The `--source-files` option specifies the number of source files per project. Default is 3.

The `--http-repo` option generates an additional build that produces an HTTP repository that provides external libraries. This repository and its classes are referenced by the generated build. Use `gradle -p repo run` to build and start the HTTP server and libraries. Only available for Java and Android builds.

##### Java specific options

The `--builds` option specifies the number of builds to generate. Set to greater than 1 to generate a composite build. Default is 1. Only available for Java builds.

### Android specific options

The `--java` option includes some Java libraries in an Android build. Default is false. 

The `--version` option specifies the Android plugin version to use. Default is 2.3.1.

### Swift specific options

The `--swift-pm` option uses Swift package manager source conventions, and also generates a Swift PM build file.

#### Add source files to an existing build

`build-builder add-source [options]`

The `--dir` option specifies the directory containing the build to add source files to. Default is the current directory.

The `--source-files` option specifies the number of source files per project. Default is 3.

### Build structure and dependency graph

The root project will define an application of the relevant type, and all other projects will define a library of the relevant type. 

#### Project dependency graph

- The application depends either directly or indirectly on all library projects
- Libraries are arranged in layers of 3 - 6 projects.
- The bottom-most libraries, with no dependencies, are suffixed with `core`.

Here's an example: 

<img src="https://rawgit.com/adammurdoch/build-builder/master/src/doc/projects.svg">
           
#### Dependencies between source files

- Each application has a main class. The remaining classes in the project, if any, are implementation classes.
- Each library has an API class, used by other projects. The remaining classes in the project, if any, are implementation classes and are not used directly by any other project. They are used indirectly.
- The classes for a project are arranged in layers of 3 - 6 classes.
- The main class/API class uses the first layer of implementation classes.
- The implementation classes in the second last layer use the API class for each library that the project depends on.
    - For Android projects, this class also uses the generated `R` class.
- The implementation classes in the last layer have no dependencies, and are suffixed with `NoDeps`.

Here's an example:

<img src="https://rawgit.com/adammurdoch/build-builder/master/src/doc/sources.svg">

### Current limitations

#### Generating builds

- Android application 
    - Only a single Java project per layer.
    - Only a single instrumented test per project, doesn't do anything.
    - No multi-dex, multi-apk splits, instant app
- No annotation processors are used.
- External HTTP repo
    - Has fixed size and structure, only a small number of libraries.
    - Not available for C++ or Swift
    - Dependencies used by 'impl' class only, and this project uses all libraries from the repo directly rather than some set of API libraries
- Composite builds
    - Doesn't generate a library project with `--projects 1` (the default)
    - Not available for C++, Swift or Android
    - Dependencies used by 'impl' class only, and this project uses all libraries from the repo directly rather than some set of API libraries
- External dependencies are the same for all projects.
    - Only a small number of external dependencies
    - slf4j
    - support-core-utils (Android builds only)
- There are no external dependencies for C++ or Swift
- There are no tests for C++ or Swift
- There are no private headers for C++ libraries
- Only a basic dependency graph is available, between projects and between source files and external libraries
    - Arranged in layers 
    - Only one layer of a project references classes from other projects
- Generated classes are small.
- There are no transitive API classes. 
- There are no type hierarchies.
- No Java 8 source for Android or Java builds.
- Only a single Java resource is generated for each project.
- Implementation classes are public.
- Improve project and source file names, particularly test files.

#### Adding source to an existing build

- Adding source to an existing build does not consider project dependencies.
- Adding source to a native build does nothing.
