[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.appgallery.internal](../index.md) / [AppGalleryService](./index.md)

# AppGalleryService

`interface AppGalleryService`

### Functions

| Name | Summary |
|---|---|
| [acquireToken](acquire-token.md) | `abstract fun acquireToken(tokenBody: `[`TokenBody`](../-token-body/index.md)`): Call<`[`TokenResult`](../-token-result/index.md)`>` |
| [deleteListing](delete-listing.md) | `abstract fun deleteListing(appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, lang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`CommonResult`](../-common-result/index.md)`>` |
| [getAppId](get-app-id.md) | `abstract fun getAppId(packageName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`AppsIdsResult`](../-apps-ids-result/index.md)`>` |
| [getListings](get-listings.md) | `abstract fun getListings(appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`ListingResult`](../-listing-result/index.md)`>` |
| [getUploadUrl](get-upload-url.md) | `abstract fun getUploadUrl(appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, suffix: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`UploadUrlResult`](../-upload-url-result/index.md)`>` |
| [submit](submit.md) | `abstract fun submit(appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, releaseType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, requestBody: RequestBody): Call<`[`CommonResult`](../-common-result/index.md)`>` |
| [updateAppFileInfo](update-app-file-info.md) | `abstract fun updateAppFileInfo(appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, appInfoFiles: `[`AppInfoFilesBody`](../-app-info-files-body/index.md)`): Call<`[`CommonResult`](../-common-result/index.md)`>` |
| [updateChangelog](update-changelog.md) | `abstract fun updateChangelog(appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, changelogBody: `[`ChangelogBody`](../-changelog-body/index.md)`): Call<`[`CommonResult`](../-common-result/index.md)`>` |
| [updateListings](update-listings.md) | `abstract fun updateListings(appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, listingBody: `[`ListingBody`](../-listing-body/index.md)`): Call<`[`CommonResult`](../-common-result/index.md)`>` |
| [upload](upload.md) | `abstract fun upload(uploadUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, file: Part, name: RequestBody, authCode: RequestBody, fileCount: RequestBody): Call<`[`UploadFileResult`](../-upload-file-result/index.md)`>` |
