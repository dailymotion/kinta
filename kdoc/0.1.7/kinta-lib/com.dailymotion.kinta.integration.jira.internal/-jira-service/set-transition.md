[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.jira.internal](../index.md) / [JiraService](index.md) / [setTransition](./set-transition.md)

# setTransition

`@POST("issue/{issueId}/transitions") abstract fun setTransition(@Path("issueId") issueId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Body transitionBody: `[`TransitionBody`](../-transition-body/index.md)`): Call<`[`Void`](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)`>`