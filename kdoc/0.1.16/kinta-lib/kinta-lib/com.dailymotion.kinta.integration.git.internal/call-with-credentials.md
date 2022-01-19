//[kinta-lib](../../index.md)/[com.dailymotion.kinta.integration.git.internal](index.md)/[callWithCredentials](call-with-credentials.md)



# callWithCredentials  
[jvm]  
Content  
fun PushCommand.[callWithCredentials](call-with-credentials.md)(): [MutableIterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-iterable/index.html)<PushResult>  
More info  


execute the given pushCommand against a HTTPS remote By default, Jgit uses the origin remote to push. It might be configured using a ssh config that Jgit does not understand. This method will create a temporary HTTPS remote to push against

  


[jvm]  
Content  
fun PullCommand.[callWithCredentials](call-with-credentials.md)(): PullResult  
More info  


execute the given pullCommand against a HTTPS remote By default, Jgit uses the origin remote to pull. It might be configured using a ssh config that Jgit does not understand. This method will create a temporary HTTPS remote to pull against

  


[jvm]  
Content  
fun FetchCommand.[callWithCredentials](call-with-credentials.md)(): FetchResult  
More info  


execute the given fetch against a HTTPS remote By default, Jgit uses the origin remote to fetch. It might be configured using a ssh config that Jgit does not understand. This method will create a temporary HTTPS remote to fetch against

  



