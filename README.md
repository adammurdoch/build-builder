## build builder

Generates various builds that can be used for profiling and benchmarking Gradle.

Supported build types:

- Java
- Android
- C++

Generates one or more projects with source files. Can also be used to add source files to an existing skeleton build. 
The source files have dependencies between each other, as described below.

Generates JUnit tests for Java and Android projects.

Generates a [gradle-profiler](https://www.github.com/gradle/gradle-profiler) scenario file for the build.

### Command line usage

#### Create a build

`build-builder java|cpp|android [options]`

The `--dir` option specifies the directory to create the build init. Default is the current directory.

The `--projects` option specifies the number of projects. Default is 1.

The `--source-files` option specifies the number of source files per project. Default is 3.

The `--java` option includes some Java libraries in an Android build. Default is false. 

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

- Adding source to an existing build does not consider project dependencies.
- Adding source to a native build does nothing.
- The Android application does not actually work. The Java and C++ applications can be installed and executed.
    - Can include only a single Java project.
    - There are no instrumented tests.
- No annotation processors are used.
- External dependencies are the same for all projects and not used by the source.
- External HTTP repo is fixed size.
- There are no external dependencies for C++.
- There are no tests for C++
- Only a basic dependency graph is available, between projects and between source files and external libraries
    - Arranged in layers 
    - Only one layer of a project references classes from other projects
- Generated classes are small.
- There are no transitive API classes. 
