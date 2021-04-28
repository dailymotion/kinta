[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.transifex.internal.model](../index.md) / [TransifexService](index.md) / [pushTranslation](./push-translation.md)

# pushTranslation

`@PUT("{projectSlug}/resource/{resource}/translation/{txLang}") abstract fun pushTranslation(@Path("projectSlug") projectSlug: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Path("resource") resource: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Path("txLang") txLang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Body txTranslation: `[`TxTranslation`](../-tx-translation/index.md)`): Call<`[`Void`](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)`>`