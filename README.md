## build builder

Generates various builds that can be used for profiling and benchmarking Gradle.

- Java
- Android
- C++

### Command line options

The `--projects` option can be used to specify the number of projects. The root project will contain an application of the relevant type, and all other projects will contain a library of the relevant type. 

Dependencies between source files:

- The application project depends on all of the library projects except `core`
- All of the library projects depend on `core`
- Each application has a main class (or function) and an implementation class.
- Each library has an API class and an implementation class.
- The main class/API class/main function uses the implementation class.
- The implementation class uses the API class for each library that the project depends on.

### Current limitations

- The Android application does not actually work. The other applications can be installed and executed.
    - The `R` and other generated classes are not referenced.
    - No annotation processors are used.
    - No Java library projects are included.
- There are no external dependencies.
- There are no tests.
- Only a shallow and wide dependency graph is available.
- There are no transitive API classes. 
