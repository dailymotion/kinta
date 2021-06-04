//[kinta-lib](../../index.md)/[com.dailymotion.kinta.integration.jira.internal](index.md)



# Package com.dailymotion.kinta.integration.jira.internal  


## Types  
  
|  Name |  Summary | 
|---|---|
| <a name="com.dailymotion.kinta.integration.jira.internal/AssignBody///PointingToDeclaration/"></a>[AssignBody](-assign-body/index.md)| <a name="com.dailymotion.kinta.integration.jira.internal/AssignBody///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [AssignBody](-assign-body/index.md)(**name**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))  <br><br><br>|
| <a name="com.dailymotion.kinta.integration.jira.internal/CommentBody///PointingToDeclaration/"></a>[CommentBody](-comment-body/index.md)| <a name="com.dailymotion.kinta.integration.jira.internal/CommentBody///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [CommentBody](-comment-body/index.md)(**body**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))  <br><br><br>|
| <a name="com.dailymotion.kinta.integration.jira.internal/Issue///PointingToDeclaration/"></a>[Issue](-issue/index.md)| <a name="com.dailymotion.kinta.integration.jira.internal/Issue///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [Issue](-issue/index.md)(**id**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **fields**: [Issue.IssueFields](-issue/-issue-fields/index.md))  <br><br><br>|
| <a name="com.dailymotion.kinta.integration.jira.internal/JiraService///PointingToDeclaration/"></a>[JiraService](-jira-service/index.md)| <a name="com.dailymotion.kinta.integration.jira.internal/JiraService///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>interface [JiraService](-jira-service/index.md)  <br><br><br>|
| <a name="com.dailymotion.kinta.integration.jira.internal/Status///PointingToDeclaration/"></a>[Status](-status/index.md)| <a name="com.dailymotion.kinta.integration.jira.internal/Status///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [Status](-status/index.md)(**id**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **name**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))  <br><br><br>|
| <a name="com.dailymotion.kinta.integration.jira.internal/Transition///PointingToDeclaration/"></a>[Transition](-transition/index.md)| <a name="com.dailymotion.kinta.integration.jira.internal/Transition///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [Transition](-transition/index.md)(**id**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **to**: [Status](-status/index.md)?)  <br><br><br>|
| <a name="com.dailymotion.kinta.integration.jira.internal/TransitionBody///PointingToDeclaration/"></a>[TransitionBody](-transition-body/index.md)| <a name="com.dailymotion.kinta.integration.jira.internal/TransitionBody///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [TransitionBody](-transition-body/index.md)(**transition**: [Transition](-transition/index.md))  <br><br><br>|
| <a name="com.dailymotion.kinta.integration.jira.internal/TransitionResult///PointingToDeclaration/"></a>[TransitionResult](-transition-result/index.md)| <a name="com.dailymotion.kinta.integration.jira.internal/TransitionResult///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [TransitionResult](-transition-result/index.md)(**transitions**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Transition](-transition/index.md)>)  <br><br><br>|

