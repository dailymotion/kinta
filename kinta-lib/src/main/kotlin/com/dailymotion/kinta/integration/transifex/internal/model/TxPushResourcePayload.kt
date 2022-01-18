package com.dailymotion.kinta.integration.transifex.internal.model

import kotlinx.serialization.Serializable

@Serializable
data class TxPushResourcePayload(val data: TxPushResourcePayloadData) {

    @Serializable
    data class TxPushResourcePayloadData(
        val attributes: TxAttributes,
        val relationships: TxRelationShips,
        val type: String
    )

    @Serializable
    data class TxAttributes(
        val content: String,
        val content_encoding: String = "text",
        val file_type: String?,
    )

    companion object {

        private const val TYPE_SOURCE = "resource_strings_async_uploads"
        private const val TYPE_TRANSLATION = "resource_translations_async_uploads"

        fun createTranslationPayload(
            org: String,
            project: String,
            resource: String,
            content: String,
            lang: String
        ): TxPushResourcePayload {
            return createPayload(org, project, resource, content, lang, TYPE_TRANSLATION)
        }

        fun createSourcePayload(
            org: String,
            project: String,
            resource: String,
            content: String
        ): TxPushResourcePayload {
            return createPayload(org, project, resource, content, null, TYPE_SOURCE)
        }

        private fun createPayload(
            org: String,
            project: String,
            resource: String,
            content: String,
            lang: String?,
            type: String
        ): TxPushResourcePayload {
            return TxPushResourcePayload(
                TxPushResourcePayloadData(
                    type = type,
                    attributes = TxAttributes(
                        content = content,
                        file_type = lang?.let { "default" } /** Quite strange, for a translation upload, file_type has to be set **/
                    ),
                    relationships = TxRelationShips(
                        language = lang?.let {
                            TxRelationShipsItem(
                                TxRelationShipsItemData(
                                    id = "l:$lang",
                                    type = "languages"
                                )
                            )
                        },
                        resource = TxRelationShipsItem(
                            TxRelationShipsItemData(
                                id = "o:$org:p:$project:r:$resource",
                                type = "resources"
                            )
                        )
                    )
                )
            )
        }
    }
}
