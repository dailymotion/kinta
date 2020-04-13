[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.git.model](../index.md) / [BranchInfo](./index.md)

# BranchInfo

`data class BranchInfo`

### Parameters

`name` - : the name of the branch

`pullRequests` - : the pull requests that have `name` as HEAD

`dependentPullRequests` - : the pull requests that have `name` as BASE

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BranchInfo(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, pullRequests: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`PullRequestInfo`](../-pull-request-info/index.md)`>, dependentPullRequests: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`PullRequestInfo`](../-pull-request-info/index.md)`>)` |

### Properties

| Name | Summary |
|---|---|
| [dependentPullRequests](dependent-pull-requests.md) | : the pull requests that have `name` as BASE`val dependentPullRequests: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`PullRequestInfo`](../-pull-request-info/index.md)`>` |
| [name](name.md) | : the name of the branch`val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [pullRequests](pull-requests.md) | : the pull requests that have `name` as HEAD`val pullRequests: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`PullRequestInfo`](../-pull-request-info/index.md)`>` |
