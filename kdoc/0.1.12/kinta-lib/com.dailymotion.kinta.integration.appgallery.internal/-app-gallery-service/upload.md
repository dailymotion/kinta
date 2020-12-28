[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.appgallery.internal](../index.md) / [AppGalleryService](index.md) / [upload](./upload.md)

# upload

`@Multipart @POST abstract fun upload(@Url uploadUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Part file: Part, @Part("name") name: RequestBody, @Part("authCode") authCode: RequestBody, @Part("fileCount") fileCount: RequestBody): Call<`[`UploadFileResult`](../-upload-file-result/index.md)`>`