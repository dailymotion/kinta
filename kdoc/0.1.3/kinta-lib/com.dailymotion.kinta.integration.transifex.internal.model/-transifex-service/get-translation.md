[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.transifex.internal.model](../index.md) / [TransifexService](index.md) / [getTranslation](./get-translation.md)

# getTranslation

`@GET("{projectSlug}/resource/{resource}/translation/{txLang}") abstract fun getTranslation(@Path("projectSlug") projectSlug: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Path("resource") resource: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Path("txLang") txLang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Query("mode") mode: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): Call<`[`TxTranslation`](../-tx-translation/index.md)`>`