[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.jira.internal](../index.md) / [JiraService](./index.md)

# JiraService

`interface JiraService`

### Functions

| Name | Summary |
|---|---|
| [addComment](add-comment.md) | `abstract fun addComment(issueId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, commentBody: `[`CommentBody`](../-comment-body/index.md)`): Call<`[`Void`](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)`>` |
| [assign](assign.md) | `abstract fun assign(issueId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, assignBody: `[`AssignBody`](../-assign-body/index.md)`): Call<`[`Void`](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)`>` |
| [getIssue](get-issue.md) | `abstract fun getIssue(issueId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`Issue`](../-issue/index.md)`>` |
| [getTransitions](get-transitions.md) | `abstract fun getTransitions(issueId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`TransitionResult`](../-transition-result/index.md)`>` |
| [setTransition](set-transition.md) | `abstract fun setTransition(issueId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, transitionBody: `[`TransitionBody`](../-transition-body/index.md)`): Call<`[`Void`](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)`>` |
