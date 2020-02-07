[kinta-lib](../../../index.md) / [com.dailymotion.kinta](../../index.md) / [GetBranchInfos](../index.md) / [Ref](./index.md)

# Ref

`data class Ref`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Ref(__typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, associatedPullRequests: AssociatedPullRequest)` |

### Properties

| Name | Summary |
|---|---|
| [__typename](__typename.md) | `val __typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [associatedPullRequests](associated-pull-requests.md) | A list of pull requests with this ref as the head ref.`val associatedPullRequests: AssociatedPullRequest` |
| [name](name.md) | The ref name.`val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [marshaller](marshaller.md) | `fun marshaller(): ResponseFieldMarshaller` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [invoke](invoke.md) | `operator fun invoke(reader: ResponseReader): Ref` |
