package com.dailymotion.kinta.integration.lokalise.internal.model

import kotlinx.serialization.Serializable
import java.io.File


@Serializable
data class LkSupportedLanguagesResponse(val languages: List<LkLanguage>){
    @Serializable
    data class LkLanguage(
        val lang_iso: String
    )
}

data class LkLangResponse(
    val file: File,
    val lang_iso: String,
)

@Serializable
data class LkDownloadPayload(
    val filter_langs: List<String>,
    val filter_filenames: List<String>,
    val format: String,
    val directory_prefix: String = "%LANG_ISO%",
)

@Serializable
data class LkUploadPayload(
    val data: String,
    val filename: String,
    val lang_iso: String,
    val replace_modified: Boolean = true,
)