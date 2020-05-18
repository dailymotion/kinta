[kinta-workflows](../../index.md) / [com.dailymotion.kinta.workflows.builtin.playstore](../index.md) / [LocalMetadataHelper](./index.md)

# LocalMetadataHelper

`object LocalMetadataHelper`

### Properties

| Name | Summary |
|---|---|
| [ANDROID_METADATA_FOLDER](-a-n-d-r-o-i-d_-m-e-t-a-d-a-t-a_-f-o-l-d-e-r.md) | `val ANDROID_METADATA_FOLDER: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html) |

### Functions

| Name | Summary |
|---|---|
| [cleanMetaDatas](clean-meta-datas.md) | `fun cleanMetaDatas(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getAllListing](get-all-listing.md) | Retrieve local listings metadatas`fun getAllListing(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ListingResource>` |
| [getChangelog](get-changelog.md) | Retrieve all localized changeLogs for a specific version code`fun getChangelog(versionCode: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ChangelogResource>` |
| [getImages](get-images.md) | Retrieve local images metadata`fun getImages(languageCode: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, imageType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ImageUploadData>` |
| [saveChangeLogs](save-change-logs.md) | `fun saveChangeLogs(changeLogs: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ChangelogResource>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [saveListings](save-listings.md) | `fun saveListings(listings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ListingResource>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
