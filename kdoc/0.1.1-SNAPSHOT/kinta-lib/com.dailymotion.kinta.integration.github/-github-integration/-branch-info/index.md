[kinta-lib](../../../index.md) / [com.dailymotion.kinta.integration.github](../../index.md) / [GithubIntegration](../index.md) / [BranchInfo](./index.md)

# BranchInfo

`data class BranchInfo`

### Parameters

`name` - : the name of the branch

`pullRequests` - : the pull requests associated with a given branch

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BranchInfo(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, pullRequests: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PullRequestInfo>, dependantPullRequests: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PullRequestInfo>)` |

### Properties

| Name | Summary |
|---|---|
| [dependantPullRequests](dependant-pull-requests.md) | `val dependantPullRequests: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PullRequestInfo>` |
| [name](name.md) | : the name of the branch`val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [pullRequests](pull-requests.md) | : the pull requests associated with a given branch`val pullRequests: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PullRequestInfo>` |