package com.dailymotion.kinta.integration.transifex.internal.model

import kotlinx.serialization.Serializable

@Serializable
data class TxPullResourcePayload(val data: TxPullResourcePayloadData) {

    @Serializable
    data class TxPullResourcePayloadData(
        val attributes: TxAttributes,
        val relationships: TxRelationShips,
        val type: String
    )

    @Serializable
    data class TxAttributes(
        val callback_url: String? = null,
        val content_encoding: String = "text",
        val file_type: String = "default",
        val mode: String? = null,
        val pseudo: Boolean = false
    )

    companion object {

        private const val TYPE_SOURCE = "resource_strings_async_downloads"
        private const val TYPE_TRANSLATION = "resource_translations_async_downloads"

        fun createSourcePayload(
            org: String,
            project: String,
            resource: String,
        ): TxPullResourcePayload {
            return createPayload(
                org = org,
                project = project,
                resource = resource,
                type = TYPE_SOURCE
            )
        }

        fun createTranslationPayload(
            org: String,
            project: String,
            resource: String,
            lang: String,
            mode: String = "default"
        ): TxPullResourcePayload {
            return createPayload(
                org = org,
                project = project,
                resource = resource,
                lang = lang,
                mode = mode,
                type = TYPE_TRANSLATION
            )
        }

        private fun createPayload(
            org: String,
            project: String,
            resource: String,
            lang: String? = null,
            mode: String? = null,
            type: String,
        ): TxPullResourcePayload {
            return TxPullResourcePayload(
                TxPullResourcePayloadData(
                    attributes = TxAttributes(
                        mode = mode
                    ),
                    type = type,
                    relationships = TxRelationShips(
                        resource = TxRelationShipsItem(
                            TxRelationShipsItemData(
                                id = "o:$org:p:$project:r:$resource",
                                type = "resources"
                            )
                        ),
                        language = lang?.let {
                            TxRelationShipsItem(
                                TxRelationShipsItemData(
                                    id = "l:$it",
                                    type = "languages"
                                )
                            )
                        }
                    )
                )
            )
        }
    }
}
