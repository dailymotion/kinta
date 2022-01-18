package com.dailymotion.kinta.integration.transifex.internal.model

import kotlinx.serialization.Serializable


@Serializable
data class TxTranslation(val content: String?)

/** Models for supported languages response **/
@Serializable
data class TxSupportedLanguagesResponse(val data: List<TxLanguage>)

@Serializable
data class TxLanguage(val id: String){
    val code = id.removePrefix("l:")
}

/** RelationShips dedicated models **/
@Serializable
data class TxRelationShips(
    val language: TxRelationShipsItem? = null,
    val resource: TxRelationShipsItem? = null
)

@Serializable
data class TxRelationShipsItem(
    val data: TxRelationShipsItemData
)

@Serializable
data class TxRelationShipsItemData(
    val id: String,
    val type: String
)