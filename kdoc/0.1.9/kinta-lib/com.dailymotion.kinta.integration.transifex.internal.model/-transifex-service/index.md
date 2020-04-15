[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.transifex.internal.model](../index.md) / [TransifexService](./index.md)

# TransifexService

`interface TransifexService`

### Functions

| Name | Summary |
|---|---|
| [getLanguages](get-languages.md) | `abstract fun getLanguages(projectSlug: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`TxLanguage`](../-tx-language/index.md)`>>` |
| [getTranslation](get-translation.md) | `abstract fun getTranslation(projectSlug: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, resource: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, txLang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mode: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): Call<`[`TxTranslation`](../-tx-translation/index.md)`>` |
| [pushSource](push-source.md) | `abstract fun pushSource(projectSlug: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, resource: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, txTranslation: `[`TxTranslation`](../-tx-translation/index.md)`): Call<`[`Void`](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)`>` |
| [pushTranslation](push-translation.md) | `abstract fun pushTranslation(projectSlug: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, resource: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, txLang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, txTranslation: `[`TxTranslation`](../-tx-translation/index.md)`): Call<`[`Void`](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)`>` |
