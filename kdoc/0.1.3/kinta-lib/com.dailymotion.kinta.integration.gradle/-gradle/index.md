[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.gradle](../index.md) / [Gradle](./index.md)

# Gradle

`class Gradle`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Gradle(directory: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)`? = null, useLoggingLevelQuiet: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = Logger.level > Logger.LEVEL_INFO)` |

### Properties

| Name | Summary |
|---|---|
| [useLoggingLevelQuiet](use-logging-level-quiet.md) | `val useLoggingLevelQuiet: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Functions

| Name | Summary |
|---|---|
| [executeGroupTasks](execute-group-tasks.md) | `fun executeGroupTasks(groupName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, vararg tasks: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [executeTask](execute-task.md) | `fun executeTask(task: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [executeTasks](execute-tasks.md) | `fun executeTasks(vararg tasks: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
