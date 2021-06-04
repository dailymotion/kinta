//[kinta-lib](../../../index.md)/[com.dailymotion.kinta.integration.git.model](../index.md)/[BranchInfo](index.md)



# BranchInfo  
 [jvm] data class [BranchInfo](index.md)(**name**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **pullRequests**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[PullRequestInfo](../-pull-request-info/index.md)>, **dependentPullRequests**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[PullRequestInfo](../-pull-request-info/index.md)>)   


## Parameters  
  
jvm  
  
| | |
|---|---|
| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo///PointingToDeclaration/"></a>name| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo///PointingToDeclaration/"></a><br><br>: the name of the branch<br><br>|
| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo///PointingToDeclaration/"></a>pullRequests| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo///PointingToDeclaration/"></a><br><br>: the pull requests that have name as HEAD<br><br>|
| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo///PointingToDeclaration/"></a>dependentPullRequests| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo///PointingToDeclaration/"></a><br><br>: the pull requests that have name as BASE<br><br>|
  


## Constructors  
  
| | |
|---|---|
| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo/BranchInfo/#kotlin.String#kotlin.collections.List[com.dailymotion.kinta.integration.git.model.PullRequestInfo]#kotlin.collections.List[com.dailymotion.kinta.integration.git.model.PullRequestInfo]/PointingToDeclaration/"></a>[BranchInfo](-branch-info.md)| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo/BranchInfo/#kotlin.String#kotlin.collections.List[com.dailymotion.kinta.integration.git.model.PullRequestInfo]#kotlin.collections.List[com.dailymotion.kinta.integration.git.model.PullRequestInfo]/PointingToDeclaration/"></a> [jvm] fun [BranchInfo](-branch-info.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), pullRequests: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[PullRequestInfo](../-pull-request-info/index.md)>, dependentPullRequests: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[PullRequestInfo](../-pull-request-info/index.md)>): the name of the branch   <br>|


## Properties  
  
|  Name |  Summary | 
|---|---|
| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo/dependentPullRequests/#/PointingToDeclaration/"></a>[dependentPullRequests](dependent-pull-requests.md)| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo/dependentPullRequests/#/PointingToDeclaration/"></a> [jvm] val [dependentPullRequests](dependent-pull-requests.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[PullRequestInfo](../-pull-request-info/index.md)>: the pull requests that have name as BASE   <br>|
| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo/name/#/PointingToDeclaration/"></a>[name](name.md)| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo/name/#/PointingToDeclaration/"></a> [jvm] val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html): the name of the branch   <br>|
| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo/pullRequests/#/PointingToDeclaration/"></a>[pullRequests](pull-requests.md)| <a name="com.dailymotion.kinta.integration.git.model/BranchInfo/pullRequests/#/PointingToDeclaration/"></a> [jvm] val [pullRequests](pull-requests.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[PullRequestInfo](../-pull-request-info/index.md)>: the pull requests that have name as HEAD   <br>|

