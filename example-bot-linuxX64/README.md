# Example: Linux x64 echo bot

This bot runs natively on Linux, and it echos back messages sent to the bot.

## Usage

This bot requires signald to be installed with an account that's already registered with signald.

From the root project directory,

- Run `./gradlew :example-bot-linuxX64:linkReleaseExecutableLinuxX64`
- Inside of `example-bot-linuxX64/build/bin/linuxX64/releaseExecutable` is `example-bot-linuxX64.kexe`, which is an
  executable. 

  To run the bot, run `./example-bot-linuxX64.kexe +<number>`
