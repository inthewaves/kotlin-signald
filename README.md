# kotlin-signald

A Kotlin multiplatform library for communicating with signald.

## Using in your projects

> This library is experimental, with the API subject to breaking changes.

### Gradle

- Add the Maven Central repository if it is not already there:

    ```kotlin
    repositories {
        mavenCentral()
    }
    ```

- In multiplatform projects, add a dependency to the `commonMain` source set dependencies
    
    ```kotlin
    kotlin {
        sourceSets {
            commonMain {
                 dependencies {
                     implementation("org.inthewaves.kotlin-signald:clientprotocol:0.2.0")
                 }
            }
        }
    }
    ```

- For single-platform projects such as JVM, add a dependency to the dependencies block.

    ```groovy
    dependencies {
        implementation("org.inthewaves.kotlin-signald:clientprotocol:0.2.0")
    }
    ```

## Building

The signald classes are generated from the protocol JSON file, located in
[`clientprotocol/protocol.json`](clientprotocol/protocol.json).

To regenerate the classes, 

* Replace `protocol.json` with a newer version
* Run `./gradlew generateSignaldClasses`
* Reformat the code with `./gradlew :clientprotocol:ktlintFormat` 
* Find and replace instances of <code>\`data\`:</code> with `data:`

### Compiling dynamic library

Ensure you have these dependencies. For Debian,

```bash
sudo apt install libncurses5
```

Then, run `./gradlew linkLinuxX64`.
