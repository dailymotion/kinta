//[kinta-lib](../../../index.md)/[com.dailymotion.kinta.integration.gitlab.internal](../index.md)/[GitlabService](index.md)/[getMRWithHead](get-m-r-with-head.md)



# getMRWithHead  
[jvm]  
Content  
@GET(value = projects/{projectId}/merge_requests)  
  
abstract fun [getMRWithHead](get-m-r-with-head.md)(@Path(value = projectId)projectId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Query(value = source_branch)source: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): Call<[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[MergeRequest](../-merge-request/index.md)>>  



