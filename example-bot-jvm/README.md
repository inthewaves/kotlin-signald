# Example: JVM echo bot

This bot runs on JVM, and it echos back messages sent to the bot.

## Usage

This bot requires signald to be installed with an account that's already registered with signald.

From the root project directory,

- Run `./gradlew :example-bot-jvm:shadowJar`
- Run the bot by executing 
  `java -jar build/libs/example-bot-jvm-0.1.0-SNAPSHOT-all.jar +<accountID> [optional receiver type]`.
  The optional receiver type can be used to configure the message subscription handler type. Put in no arguments to see
  all the possible options.