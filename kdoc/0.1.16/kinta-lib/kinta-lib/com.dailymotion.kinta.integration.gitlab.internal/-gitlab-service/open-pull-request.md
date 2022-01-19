//[kinta-lib](../../../index.md)/[com.dailymotion.kinta.integration.gitlab.internal](../index.md)/[GitlabService](index.md)/[openPullRequest](open-pull-request.md)



# openPullRequest  
[jvm]  
Content  
@POST(value = projects/{projectId}/merge_requests)  
  
abstract fun [openPullRequest](open-pull-request.md)(@Path(value = projectId)projectId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Body()mergeRequestBody: [MergeRequestBody](../-merge-request-body/index.md)): Call<ResponseBody>  



