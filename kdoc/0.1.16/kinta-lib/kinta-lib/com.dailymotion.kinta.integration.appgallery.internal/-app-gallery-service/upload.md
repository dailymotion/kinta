//[kinta-lib](../../../index.md)/[com.dailymotion.kinta.integration.appgallery.internal](../index.md)/[AppGalleryService](index.md)/[upload](upload.md)



# upload  
[jvm]  
Content  
@Multipart()  
@POST()  
  
abstract fun [upload](upload.md)(@Url()uploadUrl: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Part()file: MultipartBody.Part, @Part(value = name)name: RequestBody, @Part(value = authCode)authCode: RequestBody, @Part(value = fileCount)fileCount: RequestBody): Call<[UploadFileResult](../-upload-file-result/index.md)>  



