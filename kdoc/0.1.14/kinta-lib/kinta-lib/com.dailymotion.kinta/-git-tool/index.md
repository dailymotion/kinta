//[kinta-lib](../../../index.md)/[com.dailymotion.kinta](../index.md)/[GitTool](index.md)



# GitTool  
 [jvm] interface [GitTool](index.md)   


## Functions  
  
|  Name |  Summary | 
|---|---|
| <a name="com.dailymotion.kinta/GitTool/deleteRef/#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String/PointingToDeclaration/"></a>[deleteRef](delete-ref.md)| <a name="com.dailymotion.kinta/GitTool/deleteRef/#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [deleteRef](delete-ref.md)(token: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, owner: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, repo: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, ref: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))  <br><br><br>|
| <a name="com.dailymotion.kinta/GitTool/getAllBranches/#kotlin.String?#kotlin.String?#kotlin.String?/PointingToDeclaration/"></a>[getAllBranches](get-all-branches.md)| <a name="com.dailymotion.kinta/GitTool/getAllBranches/#kotlin.String?#kotlin.String?#kotlin.String?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [getAllBranches](get-all-branches.md)(token: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, owner: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, repo: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>  <br><br><br>|
| <a name="com.dailymotion.kinta/GitTool/getBranchInfo/#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String#kotlin.String/PointingToDeclaration/"></a>[getBranchInfo](get-branch-info.md)| <a name="com.dailymotion.kinta/GitTool/getBranchInfo/#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [getBranchInfo](get-branch-info.md)(token: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, owner: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, repo: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, remote: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = "origin", branch: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [BranchInfo](../../com.dailymotion.kinta.integration.git.model/-branch-info/index.md)  <br><br><br>|
| <a name="com.dailymotion.kinta/GitTool/openPullRequest/#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String?/PointingToDeclaration/"></a>[openPullRequest](open-pull-request.md)| <a name="com.dailymotion.kinta/GitTool/openPullRequest/#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String?#kotlin.String?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [openPullRequest](open-pull-request.md)(token: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, owner: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, repo: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, head: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, base: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, title: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null)  <br><br><br>|


## Inheritors  
  
|  Name | 
|---|
| <a name="com.dailymotion.kinta.integration.github/GithubIntegration///PointingToDeclaration/"></a>[GithubIntegration](../../com.dailymotion.kinta.integration.github/-github-integration/index.md)|
| <a name="com.dailymotion.kinta.integration.gitlab/GitlabIntegration///PointingToDeclaration/"></a>[GitlabIntegration](../../com.dailymotion.kinta.integration.gitlab/-gitlab-integration/index.md)|

