package com.dailymotion.kinta.integration.transifex.internal.model

import kotlinx.serialization.Serializable


@Serializable
data class TxTranslation(val content: String?)
@Serializable
data class TxLanguage(val language_code: String)