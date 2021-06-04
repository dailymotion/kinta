//[kinta-lib](../../../index.md)/[com.dailymotion.kinta.integration.transifex.internal.model](../index.md)/[TransifexService](index.md)/[getTranslation](get-translation.md)



# getTranslation  
[jvm]  
Content  
@GET(value = {projectSlug}/resource/{resource}/translation/{txLang})  
  
abstract fun [getTranslation](get-translation.md)(@Path(value = projectSlug)projectSlug: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Path(value = resource)resource: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Path(value = txLang)txLang: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Query(value = mode)mode: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): Call<[TxTranslation](../-tx-translation/index.md)>  



