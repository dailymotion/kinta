[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.gitlab.internal](../index.md) / [GitlabService](index.md) / [getMRWithBase](./get-m-r-with-base.md)

# getMRWithBase

`@GET("projects/{projectId}/merge_requests") abstract fun getMRWithBase(@Path("projectId") projectId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Query("target_branch") target: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`MergeRequest`](../-merge-request/index.md)`>>`