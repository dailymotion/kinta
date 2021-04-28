[kinta-lib](../../../index.md) / [com.dailymotion.kinta](../../index.md) / [GetRefs](../index.md) / [Node1](./index.md)

# Node1

`data class Node1`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Node1(__typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, title: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, merged: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, closed: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, number: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, baseRef: BaseRef?)` |

### Properties

| Name | Summary |
|---|---|
| [__typename](__typename.md) | `val __typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [baseRef](base-ref.md) | Identifies the base Ref associated with the pull request.`val baseRef: BaseRef?` |
| [closed](closed.md) | `true` if the pull request is closed`val closed: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [merged](merged.md) | Whether or not the pull request was merged.`val merged: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [number](number.md) | Identifies the pull request number.`val number: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [title](title.md) | Identifies the pull request title.`val title: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [marshaller](marshaller.md) | `fun marshaller(): ResponseFieldMarshaller` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [invoke](invoke.md) | `operator fun invoke(reader: ResponseReader): Node1` |
