# kotlin-signald

A Kotlin multiplatform library for communicating with signald. For more information, visit
https://signald.org.

This library provides a type-safe way to communicate with a signald UNIX socket, handling
(de)serialization of responses and requests. The classes are generated from the signald protocol
document (https://signald.org/articles/protocol-documentation/).

The following platforms are supported: JVM, Linux x64, and JavaScript. (Since signald currently
works by communicating with UNIX sockets, JVM and JavaScript are effectively limited to UNIX
environments supported by signald.)

## Using in your projects

> This library is experimental, with the API subject to breaking changes.

### Gradle

- Add the Maven Central repository if it is not already there:

    ```kotlin
    repositories {
        mavenCentral()
    }
    ```

- In Kotlin Multiplatform projects, add a dependency to the `commonMain` source set dependencies
    
    ```kotlin
    kotlin {
        sourceSets {
            commonMain {
                 dependencies {
                     implementation("org.inthewaves.kotlin-signald:clientprotocol:0.3.0")
                 }
            }
        }
    }
    ```

- For single-platform projects such as JVM, add a dependency to the dependencies block.

    ```groovy
    dependencies {
        implementation("org.inthewaves.kotlin-signald:clientprotocol:0.3.0")
    }
    ```

## Development

### Generating classes

The signald classes are generated from the signald protocol JSON file, located in
[`clientprotocol/protocol.json`](./clientprotocol/protocol.json). Generation is handled by the
[`protocolgen-plugin`](./protocolgen-plugin).

To regenerate the classes, 

* Replace `protocol.json` with a newer version
* Run `./gradlew generateSignaldClasses`
* Reformat the code with `./gradlew :clientprotocol:ktlintFormat` 
* Find and replace instances of <code>\`data\`:</code> with `data:`

### Compiling a dynamic library

Kotlin/Native supports compiling the code into a dynamic library which can then be used in native
C / C++ programs. More information on this topic can be found at
https://kotlinlang.org/docs/native-dynamic-libraries.html. This library in its current state is
not fit for this purpose, but we document this here for posterity.

Ensure you have these dependencies. For Debian,

```bash
sudo apt install libncurses5
```

Then, run `./gradlew :clientprotocol:linkReleaseSharedLinuxX64`. This will generate `libkotlinsignald_api.h` and
`libkotlinsignald.so` inside of `clientprotocol/build/bin/linuxX64/releaseShared`.

As [noted on the Kotlin site](https://kotlinlang.org/docs/native-dynamic-libraries.html#generated-headers-file), the way
that Kotlin/Native exports symbols in the header file is subject to change without notice.
