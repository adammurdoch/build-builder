## build builder

Generates various builds that can be used for profiling and benchmarking Gradle.

Supported build types:

- Java
- Android
- C++

Generates one or more projects with source files. The source files have dependencies between each other, as described below.

Also generates a [gradle-profiler](https://www.github.com/gradle/gradle-profiler) scenario file for the build.

### Command line options

The `--projects` option can be used to specify the number of projects.

The `--source-files` option can be used to specify the number of source files per project.

### Build structure and dependency graph

The root project will contain an application of the relevant type, and all other projects will contain a library of the relevant type. 

Project dependency graph:

- The application depends on either directly or indirectly all library projects
- Libraries are arranged in layers of 3 - 6 projects.
- The bottom-most libraries, with no dependencies, are called `core`.

Here's an example: 

<img src="https://rawgit.com/adammurdoch/build-builder/master/src/doc/projects.svg">
           
Dependencies between source files:

- Each application has a main class (or function) and two or more implementation classes.
- Each library has an API class and two or more implementation classes.
- The classes are arranged in layers of 3 - 6 classes.
- The main class/API class/main function uses the first layer of implementation classes.
- The implementation classes in the second last layer use the API class for each library that the project depends on.
    - For Android projects, this class also uses the generated `R` class.
- The implementation classes in the last layer have no dependencies, and are suffixed with `NoDeps`.

Here's an example:

<img src="https://rawgit.com/adammurdoch/build-builder/master/src/doc/sources.svg">

### Current limitations

- The Android application does not actually work. The Java and C++ applications can be installed and executed.
    - No annotation processors are used.
    - No Java library projects are included.
    - There are no instrumented tests.
- There are no external dependencies.
- There are no tests for C++
- Only a basic dependency graph is available, between projects and between source files.
    - Arranged in layers 
    - Only one layer of a project references classes from other projects
- Generated classes are small.
- There are no transitive API classes. 
