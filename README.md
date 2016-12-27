## build builder

Generates various builds that can be used for profiling and benchmarking Gradle.

- Java
- Android
- C++

### Command line options

The `--projects` option can be used to specify the number of projects. The root project will contain an application of the relevant type, and all other projects will contain a library of the relevant type. 

### Current limitations

- The Android application does not actually work. The other applications can be installed and executed.
- There are no dependencies between projects. 
- There are no external dependencies.
- There are no tests.
