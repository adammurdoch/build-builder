## build builder

Generates various builds that can be used for profiling and benchmarking Gradle.

- Java
- Android
- C++

### Command line options

The `--projects` option can be used to specify the number of projects. The root project will contain an application of the relevant type, and all other projects will contain a library of the relevant type. 

The application project depends on all of the library projects.

### Current limitations

- The Android application does not actually work. The other applications can be installed and executed.
- There are no external dependencies.
- There are no tests.
- Only a shallow and wide dependency graph is available.
