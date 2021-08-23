# kotlin-signald

A Kotlin Multiplatform library for communicating with signald. For more information, visit https://signald.org.

This library provides a type-safe way to communicate with a signald UNIX socket, handling (de)serialization of responses
and requests. The classes are generated from the signald protocol document
(https://signald.org/articles/protocol-documentation/).

The following platforms are supported: JVM, Linux x64, macOS x64, and JavaScript. (Since signald currently works by
communicating with UNIX sockets, JVM and JavaScript are effectively limited to UNIX environments supported by signald.)

## Using in your projects

> This library is experimental, with the API subject to breaking changes.

This project comes with a client library and a coroutine-based message receiver.

It's recommended to ensure that a message receiver is active at all times, because signald will not process incoming
messages without any client subscriptions.

Incoming messages are not limited to text messages sent by a user. They can also include

* updates to groups (e.g., when someone joins a group, a message indicating new group info is sent to the group members.
  If there are no message receivers active, any group messages sent by signald after someone new joins will not be sent 
  to that new user),
* read and typing receipts;
* expiration timer changes;
* encryption information (the [Signal Protocol](https://en.wikipedia.org/wiki/Signal_Protocol) expects incoming
  messages, e.g., see the [Double Ratchet](https://signal.org/docs/specifications/doubleratchet/#double-ratchet));
* and other implicit message types.

signald will handle these incoming messages, as long as there is a message subscription.

### Client library

Ensure that signald is running and that the socket is available. The default socket paths of
`$XDG_RUNTIME_DIR/signald/signald.sock` and `/var/run/signald/signald.sock` will be tested if an explicit socket path is
not provided.

This snippet provides an overview of the client (API inspired by [pysignald](https://pypi.org/project/pysignald)).

```kotlin
import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonGroupJoinInfo
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SendResponse

// The accountId must be an E.164 identifier, i.e., a phone number
// starting with + and the country calling code.
val signal = Signal("+12223334444")
// You can also specify a custom path to the socket.
val signalWithCustomSocketPath = Signal(
  "+12223334444",
  socketPath = customPathString
)

// Register +12223334444 with Signal if it is not already registered
// and stored by signald. This will send a verification code via SMS.
// You can also specify a captcha token if it is requested, and specify
// voice verification to get a call instead of an SMS.
//
// Errors are thrown as RequestFailedExceptions (or the more general
// SignaldException, which is typealiased to IOException on JVM to blend
// in with the SocketExceptions).
try {
  signal.register()
} catch (e: RequestFailedException) {
  if (e.exception == "CaptchaRequired") {
    // https://signald.org/articles/captcha/
    //
    // Get a captcha token. It might be better to just
    // have an account setup with signald beforehand.
    //
    signal.register(captcha = myCaptchaToken)
  }
}

// Get verification code from SMS (or call)
signal.verify("some verification code")

// Send to a single user via their number. The returned [SendResponse] contains information
// about the sent message, including the message timestamp (which identifies the message),
// and the result.
val sendResponse: SendResponse = signal.send(
  recipient = Signal.Recipient.Individual(JsonAddress(number = "+15556667777")),
  messageBody = "Hello"
)

// You can also send to a single user via their UUID.
signal.send(
  recipient = Signal.Recipient.Individual(JsonAddress(uuid = "eeeeeeee-eeee-5555-5555-555555555555")),
  messageBody = "Hello"
)

// Join a group by a group link.
val joinInfo: JsonGroupJoinInfo = signal.joinGroup("https://signal.group/#<encoded>")

// Send a message to the group that we just joined
// (omitted checks to see if joining requires admin approval)
signal.send(
  recipient = Signal.Recipient.Group(groupID = joinInfo.groupID),
  messageBody = "Hello to group"
)

// Or, specify some other group ID
signal.send(
  recipient = Signal.Recipient.Group(groupID = "Enfw3fE4fUm7RcfSUhEA1c7KAGmbZC2ot4oicB0ZXuk="),
  messageBody = "Hello to another group"
)
```

#### Gradle
Add `mavenCentral()` to the dependencies block if you haven't already done so. All of the `<current version>`
placeholders can be replaced by one of the versions from the
[releases](https://github.com/inthewaves/kotlin-signald/releases)

- In Kotlin Multiplatform projects, add a dependency to the `commonMain` source set dependencies
    
    ```kotlin
    kotlin {
        sourceSets {
            commonMain {
                 dependencies {
                     implementation("org.inthewaves.kotlin-signald:client:<current version>")
                 }
            }
        }
    }
    ```

- For single-platform projects such as JVM, add a dependency to the dependencies block.

    ```groovy
    dependencies {
        implementation("org.inthewaves.kotlin-signald:client:<current version>")
    }
    ```

### Coroutine-based message receiver

This adds a coroutine-based message handler via a `SharedFlow`:

```kotlin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import org.inthewaves.kotlinsignald.CoroutineMessageSubscriptionHandler
import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ExceptionWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IncomingMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListenerState

suspend fun receiveMessages(signal: Signal) {
  coroutineScope {
    val messageHandler = CoroutineMessageSubscriptionHandler(signal = signal, coroutineScope = this)

    messageHandler.messages.collect { message ->
      when (message) {
        is IncomingMessage -> TODO()
        is ListenerState -> TODO()
        is ExceptionWrapper -> TODO()
      }
    }
  }
}
```

Note that each instance of `CoroutineMessageSubscriptionHandler` will make a new socket connection when a `Signal`
instance is passed into the constructor. The `messageHandler` variable should be reused if there is a need for multiple
subscribers to the flow.

#### Gradle

Add `mavenCentral()` to the dependencies block if you haven't already done so.

Note that you can replace dependencies on `org.inthewaves.kotlin-signald:client` with
`org.inthewaves.kotlin-signald:client-coroutines`, as the `client-coroutines` module has an API dependency scope on the
`client` module, which means the `client` module will be added to the compile classpath.

- In Kotlin Multiplatform projects, add a dependency to the `commonMain` source set dependencies

    ```kotlin
    kotlin {
        sourceSets {
            commonMain {
                 dependencies {
                     implementation("org.inthewaves.kotlin-signald:client-coroutines:<current version>")
                 }
            }
        }
    }
    ```

- For single-platform projects such as JVM, add a dependency to the dependencies block.

    ```groovy
    dependencies {
        implementation("org.inthewaves.kotlin-signald:client-coroutines:<current version>")
    }
    ```

## Development

### Generating classes

The signald classes are generated from the signald protocol JSON file, located in
[`client/protocol.json`](./client/protocol.json). Generation is handled by the
[`protocolgen-plugin`](./protocolgen-plugin).

To regenerate the classes, 

* Replace `protocol.json` with a newer version
* Run `./gradlew generateSignaldClasses`
* Reformat the code with `./gradlew :client:ktlintFormat` 
* Find and replace instances of <code>\`data\`:</code> with `data:`

### Compiling a dynamic library (experimental)

Kotlin/Native supports compiling the code into a dynamic library which can then be used in native C / C++ programs. More
information on this topic can be found at https://kotlinlang.org/docs/native-dynamic-libraries.html. This library in its
current state is not fit for this purpose, and this feature is not mature, but we document this here for posterity.

Ensure you have these dependencies. For Debian,

```bash
sudo apt install libncurses5
```

Then, run `./gradlew :client:linkReleaseSharedLinuxX64`. This will generate `libktsignald_api.h` and
`libktsignald.so` inside of `client/build/bin/linuxX64/releaseShared`.

As [noted on the Kotlin site](https://kotlinlang.org/docs/native-dynamic-libraries.html#generated-headers-file), the way
that Kotlin/Native exports symbols in the header file is subject to change without notice.
