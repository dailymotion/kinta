[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.gitlab.internal](../index.md) / [GitlabService](./index.md)

# GitlabService

`interface GitlabService`

### Functions

| Name | Summary |
|---|---|
| [deleteBranch](delete-branch.md) | `abstract fun deleteBranch(projectId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, branch: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<ResponseBody>` |
| [getBranches](get-branches.md) | `abstract fun getBranches(projectId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Branch`](../-branch/index.md)`>>` |
| [getMRWithBase](get-m-r-with-base.md) | `abstract fun getMRWithBase(projectId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, target: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`MergeRequest`](../-merge-request/index.md)`>>` |
| [getMRWithHead](get-m-r-with-head.md) | `abstract fun getMRWithHead(projectId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`MergeRequest`](../-merge-request/index.md)`>>` |
| [openPullRequest](open-pull-request.md) | `abstract fun openPullRequest(projectId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mergeRequestBody: `[`MergeRequestBody`](../-merge-request-body/index.md)`): Call<ResponseBody>` |
