[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.commandline](../index.md) / [CommandLine](index.md) / [exitCode](./exit-code.md)

# exitCode

`fun exitCode(workingDir: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)` = File("."), command: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Execute the given process

### Parameters

`commandLine` - the commandline. The parser used to tokenize `commandLine` is very basic.
If your arguments contains spaces, use @[execute](#)`fun exitCode(workingDir: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)` = File("."), vararg args: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)