[kinta-lib](../../../index.md) / [com.dailymotion.kinta](../../index.md) / [GetRefs](../index.md) / [BaseRef](./index.md)

# BaseRef

`data class BaseRef`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BaseRef(__typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [__typename](__typename.md) | `val __typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.md) | The ref name.`val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [marshaller](marshaller.md) | `fun marshaller(): ResponseFieldMarshaller` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [invoke](invoke.md) | `operator fun invoke(reader: ResponseReader): BaseRef` |
