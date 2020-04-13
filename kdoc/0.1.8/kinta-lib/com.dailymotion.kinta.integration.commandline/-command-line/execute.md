[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.commandline](../index.md) / [CommandLine](index.md) / [execute](./execute.md)

# execute

`fun execute(workingDir: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)` = projectDir, command: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Execute the given process

### Parameters

`commandLine` - the commandline. It is expected that all arguments in commandline
do not contain spaces. If they do, use @[execute](./execute.md)`fun execute(workingDir: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)` = projectDir, vararg args: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)