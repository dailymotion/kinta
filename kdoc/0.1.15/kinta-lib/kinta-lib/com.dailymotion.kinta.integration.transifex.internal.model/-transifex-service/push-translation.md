//[kinta-lib](../../../index.md)/[com.dailymotion.kinta.integration.transifex.internal.model](../index.md)/[TransifexService](index.md)/[pushTranslation](push-translation.md)



# pushTranslation  
[jvm]  
Content  
@PUT(value = {projectSlug}/resource/{resource}/translation/{txLang})  
  
abstract fun [pushTranslation](push-translation.md)(@Path(value = projectSlug)projectSlug: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Path(value = resource)resource: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Path(value = txLang)txLang: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Body()txTranslation: [TxTranslation](../-tx-translation/index.md)): Call<[Void](https://docs.oracle.com/javase/8/docs/api/java/lang/Void.html)>  



