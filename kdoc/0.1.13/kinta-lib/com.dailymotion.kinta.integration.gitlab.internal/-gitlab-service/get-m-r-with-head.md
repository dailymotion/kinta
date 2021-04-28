[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.gitlab.internal](../index.md) / [GitlabService](index.md) / [getMRWithHead](./get-m-r-with-head.md)

# getMRWithHead

`@GET("projects/{projectId}/merge_requests") abstract fun getMRWithHead(@Path("projectId") projectId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Query("source_branch") source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`MergeRequest`](../-merge-request/index.md)`>>`