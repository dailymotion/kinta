[kinta-lib](../../index.md) / [com.dailymotion.kinta](../index.md) / [GetBranchInfos](./index.md)

# GetBranchInfos

`data class GetBranchInfos : Query<Data, Data, Variables>`

### Types

| Name | Summary |
|---|---|
| [AssociatedPullRequest](-associated-pull-request/index.md) | `data class AssociatedPullRequest` |
| [Data](-data/index.md) | `data class Data : Data` |
| [Node](-node/index.md) | `data class Node` |
| [Node1](-node1/index.md) | `data class Node1` |
| [PullRequest](-pull-request/index.md) | `data class PullRequest` |
| [Ref](-ref/index.md) | `data class Ref` |
| [Repository](-repository/index.md) | `data class Repository` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GetBranchInfos(owner: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, branchName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [branchName](branch-name.md) | `val branchName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [owner](owner.md) | `val owner: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [name](name.md) | `fun name(): OperationName` |
| [operationId](operation-id.md) | `fun operationId(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [parse](parse.md) | `fun parse(source: BufferedSource, scalarTypeAdapters: ScalarTypeAdapters): Response<Data>`<br>`fun parse(source: BufferedSource): Response<Data>` |
| [queryDocument](query-document.md) | `fun queryDocument(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [responseFieldMapper](response-field-mapper.md) | `fun responseFieldMapper(): ResponseFieldMapper<Data>` |
| [variables](variables.md) | `fun variables(): Variables` |
| [wrapData](wrap-data.md) | `fun wrapData(data: Data?): Data?` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [OPERATION_ID](-o-p-e-r-a-t-i-o-n_-i-d.md) | `const val OPERATION_ID: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [OPERATION_NAME](-o-p-e-r-a-t-i-o-n_-n-a-m-e.md) | `val OPERATION_NAME: OperationName` |
| [QUERY_DOCUMENT](-q-u-e-r-y_-d-o-c-u-m-e-n-t.md) | `val QUERY_DOCUMENT: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
