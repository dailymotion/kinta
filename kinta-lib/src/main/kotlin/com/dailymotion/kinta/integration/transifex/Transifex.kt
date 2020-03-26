package com.dailymotion.kinta.integration.transifex

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.integration.transifex.internal.model.TransifexService
import com.dailymotion.kinta.integration.transifex.internal.model.TxTranslation
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object Transifex {
    fun pushSource(
            user: String? = null,
            password: String? = null,
            project: String? = null,
            resource: String,
            content: String
    ) {
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PROJECT)

        val response = service(user, password).pushSource(project_, resource, TxTranslation(content = content)).execute()

        check (response.isSuccessful) {
            "Transifex: cannot push source: ${response.code()}: ${response.errorBody()?.string()}"
        }
    }

    fun pushTranslation(
            user: String? = null,
            password: String? = null,
            project: String? = null,
            resource: String,
            lang: String,
            content: String
    ) {
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PROJECT)

        val response = service(user, password).pushTranslation(project_, resource, lang, TxTranslation(content = content)).execute()

        check (response.isSuccessful) {
            "Transifex: cannot push translation: ${response.code()}: ${response.errorBody()?.string()}"
        }
    }

    /**
     * get the languages used in the given project
     *
     * @return the list of transifex language codes
     */
    fun getLanguages(
            user: String? = null,
            password: String? = null,
            project: String? = null
    ): List<String> {
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PROJECT)

        val response = service(user, password).getLanguages(project_).execute()

        check (response.isSuccessful) {
            "Transifex: cannot get languages: ${response.code()}: ${response.errorBody()?.string()}"
        }

        return response.body()!!.map { it.language_code }
    }
    fun getTranslation(
            user: String? = null,
            password: String? = null,
            project: String? = null,
            resource: String,
            lang: String,
            mode: String? = null
    ): String {
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PROJECT)
        return service(user, password).getTranslation(project_, resource, lang, mode).execute().body()!!.content!!
    }

    fun service(
            user: String?,
            password: String?
    ): TransifexService {

        val user_ = user ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_USER)
        val password_ = password ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PASSWORD)
        val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val credential = Credentials.basic(user_, password_)
                    val newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", credential)
                            .build()

                    chain.proceed(newRequest)
                }
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.transifex.com/api/2/project/")
                .client(okHttpClient)
                .addConverterFactory(Json.nonstrict.asConverterFactory(MediaType.get("application/json")))
                .build()

        return retrofit.create(TransifexService::class.java)
    }

    val MODE_ONLYTRANSLATED = "onlytranslated"
}