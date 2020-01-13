package com.dailymotion.kinta.integration.transifex.internal.model

import retrofit2.Call
import retrofit2.http.*

interface TransifexService {
    @GET("{projectSlug}/resource/{resource}/translation/{txLang}")
    fun getTranslation(@Path("projectSlug") projectSlug: String,
                       @Path("resource") resource: String,
                       @Path("txLang") txLang: String,
                       @Query("mode") mode: String?): Call<TxTranslation>

    @PUT("{projectSlug}/resource/{resource}/content/")
    fun pushSource(@Path("projectSlug") projectSlug: String,
                   @Path("resource") resource: String,
                   @Body txTranslation: TxTranslation): Call<Void>

    @PUT("{projectSlug}/resource/{resource}/translation/{txLang}")
    fun pushTranslation(@Path("projectSlug") projectSlug: String,
                        @Path("resource") resource: String,
                        @Path("txLang") txLang: String,
                        @Body txTranslation: TxTranslation): Call<Void>

    @GET("{projectSlug}/languages/")
    fun getLanguages(@Path("projectSlug") projectSlug: String): Call<List<TxLanguage>>
}