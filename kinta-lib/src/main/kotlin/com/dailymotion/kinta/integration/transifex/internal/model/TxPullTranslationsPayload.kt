package com.dailymotion.kinta.integration.transifex.internal.model

import kotlinx.serialization.Serializable

@Serializable
data class TxPullTranslationsPayload(val data: TxPullSourcePayloadData) {

    @Serializable
    data class TxPullSourcePayloadData(
        val attributes: TxAttributes,
        val relationships: TxRelationShips,
        val type: String = "resource_translations_async_downloads")

    @Serializable
    data class TxAttributes(
        val callback_url: String? = null,
        val content_encoding: String = "text",
        val file_type: String = "default",
        val mode: String = "default",
        val pseudo: Boolean = false
    )

    companion object {
        fun with(
            org: String,
            project: String,
            resource: String,
            lang: String,
            mode: String = "default"
        ): TxPullTranslationsPayload {
            return TxPullTranslationsPayload(
                TxPullSourcePayloadData(
                    attributes = TxAttributes(
                        mode = mode
                    ),
                    relationships = TxRelationShips(
                        resource = TxRelationShipsItem(
                            TxRelationShipsItemData(
                                id = "o:$org:p:$project:r:$resource",
                                type = "resources"
                            )
                        ),
                        language = TxRelationShipsItem(
                            TxRelationShipsItemData(
                                id = "l:$lang",
                                type = "languages"
                            )
                        )
                    )
                )
            )
        }
    }
}
