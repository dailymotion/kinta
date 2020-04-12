[kinta-lib](../../../index.md) / [com.dailymotion.kinta](../../index.md) / [GetPullRequestWithBase](../index.md) / [Repository](./index.md)

# Repository

`data class Repository`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Repository(__typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, pullRequests: PullRequest)` |

### Properties

| Name | Summary |
|---|---|
| [__typename](__typename.md) | `val __typename: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [pullRequests](pull-requests.md) | A list of pull requests that have been opened in the repository.`val pullRequests: PullRequest` |

### Functions

| Name | Summary |
|---|---|
| [marshaller](marshaller.md) | `fun marshaller(): ResponseFieldMarshaller` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [invoke](invoke.md) | `operator fun invoke(reader: ResponseReader): Repository` |
