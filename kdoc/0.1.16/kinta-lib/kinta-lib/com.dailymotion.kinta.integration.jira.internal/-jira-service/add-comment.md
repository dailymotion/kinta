//[kinta-lib](../../../index.md)/[com.dailymotion.kinta.integration.jira.internal](../index.md)/[JiraService](index.md)/[addComment](add-comment.md)



# addComment  
[jvm]  
Content  
@POST(value = issue/{issueId}/comment)  
  
abstract fun [addComment](add-comment.md)(@Path(value = issueId)issueId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Body()commentBody: [CommentBody](../-comment-body/index.md)): Call<[Void](https://docs.oracle.com/javase/8/docs/api/java/lang/Void.html)>  



