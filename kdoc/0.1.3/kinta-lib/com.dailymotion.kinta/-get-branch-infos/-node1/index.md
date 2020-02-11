[kinta-lib](../../../index.md) / [com.dailymotion.kinta](../../index.md) / [GetBranchInfos](../index.md) / [Node1](./index.md)

# Node1

`data class Node1`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Node1(__typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, headRefName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, number: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, merged: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, closed: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [__typename](__typename.md) | `val __typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [closed](closed.md) | `true` if the pull request is closed`val closed: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [headRefName](head-ref-name.md) | Identifies the name of the head Ref associated with the pull request, even if the ref has been deleted.`val headRefName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [merged](merged.md) | Whether or not the pull request was merged.`val merged: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [number](number.md) | Identifies the pull request number.`val number: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [marshaller](marshaller.md) | `fun marshaller(): ResponseFieldMarshaller` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [invoke](invoke.md) | `operator fun invoke(reader: ResponseReader): Node1` |
