package com.dailymotion.kinta.integration.appgallery.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TokenResult(val access_token: String)

@Serializable
class ListingBody(
        val lang: String,
        val appName: String,
        val appDesc: String,
        val briefInfo: String)

@Serializable
class ChangelogBody(
        val lang: String,
        val newFeatures: String)

@Serializable
class TokenBody(
        val grant_type: String,
        val client_id: String,
        val client_secret: String)

@Serializable
class AppsIdsResult(val ret: CommonResult.CommonRet, val appids: List<AppId>?) {
    @Serializable
    class AppId(val value: String)
}

@Serializable
class UploadUrlResult(val ret: CommonResult.CommonRet, val uploadUrl: String?, val authCode: String?)

@Serializable
class ListingResult(val ret: CommonResult.CommonRet, val languages: List<ListingBody>?)

@Serializable
class CommonResult(val ret: CommonRet?) {

    @Serializable
    class CommonRet(val code: Int, val msg: String){
        override fun toString() = "code= $code / message=$msg"
    }

    fun isSuccess() = ret?.code == 0
}

@Serializable
class UploadFileResult(val result: UploadResult) {
    @Serializable
    class UploadResult(@SerialName("UploadFileRsp") val uploadFileRsp: UploadFileRsp?) {
        @Serializable
        class UploadFileRsp(val fileInfoList: List<FileInfo>?) {
            @Serializable
            class FileInfo(val fileDestUlr: String)
        }
    }
}

@Serializable
class AppInfoFilesBody(val fileType: String, val files: List<AppFileInfo>) {
    @Serializable
    class AppFileInfo(val fileName: String, val fileDestUrl: String)
}

class UploadData(val uploadUrl: String, val authCode: String)