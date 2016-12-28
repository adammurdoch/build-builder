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

- The application project depends on all of the library projects except `core`
- All of the library projects depend on `core`

Here's an example:

```
         +-> lib1 -+
    app -|         |-> core
         +-> lib2 -+
```
           
Dependencies between source files:

- Each application has a main class (or function) and two or more implementation classes.
- Each library has an API class and two or more implementation classes.
- The main class/API class/main function uses each of the implementation classes.
- One implementation class uses the API class for each library that the project depends on.
- The remaining implementation classes have no dependencies.

Here's an example:

```
                                             +-> lib1 nodeps
              +-> app nodeps                 |                            +-> core nodeps
              |                +-> lib1 api -+-> lib1 impl -+             |
    app main -+ -> app impl ---+                            +-> core api -+-> core impl
                               +-> lib2 api -+-> lib2 impl -+             
                                             |
                                             +-> lib2 nodeps                                                           
```

### Current limitations

- The Android application does not actually work. The other applications can be installed and executed.
    - The `R` and other generated classes are not referenced.
    - No annotation processors are used.
    - No Java library projects are included.
- There are no external dependencies.
- There are no tests.
- Only a shallow and wide dependency graph is available, between projects and between source files.
- There are no transitive API classes. 
