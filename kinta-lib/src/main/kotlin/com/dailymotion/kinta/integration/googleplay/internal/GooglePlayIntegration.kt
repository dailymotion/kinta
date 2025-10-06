@file:Suppress("DEPRECATION")

// for GoogleCredential

package com.dailymotion.kinta.integration.googleplay.internal

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.integration.googleplay.GooglePlayRelease
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.PemReader
import com.google.api.client.util.SecurityUtils
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.api.services.androidpublisher.model.Listing
import com.google.api.services.androidpublisher.model.LocalizedText
import com.google.api.services.androidpublisher.model.Track
import com.google.api.services.androidpublisher.model.TrackRelease
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.internal.impldep.org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.InputStream
import java.io.StringReader
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

object GooglePlayIntegration {

    enum class GooglePlayTrack {
        beta, production, alpha, internal
    }

    private val TRACK_BETA = "beta"
    private val TRACK_PRODUCTION = "production"
    private val json = Json { ignoreUnknownKeys = true }

    private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

    private fun publisher(googlePlayJson: String?, applicationName: String): AndroidPublisher {
        val googlePlayJson_ = googlePlayJson
                ?: KintaEnv.get(KintaEnv.Var.GOOGLE_PLAY_JSON)
                ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_JSON)

        val json = this.json.parseToJsonElement(googlePlayJson_).jsonObject
        Logger.i(String.format("Authorizing using Service Account: %s", json["client_email"]?.jsonPrimitive?.content))

        val bytes = PemReader.readFirstSectionAndClose(StringReader(json["private_key"]?.jsonPrimitive?.content ?: error("cannot find private key!!")), "PRIVATE KEY").base64DecodedBytes
        val privKey = SecurityUtils.getRsaKeyFactory().generatePrivate(PKCS8EncodedKeySpec(bytes));

        val credential = GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(json["client_email"]?.jsonPrimitive?.content)
                .setServiceAccountScopes(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
                .setServiceAccountPrivateKey(privKey)
                .build()

        return AndroidPublisher.Builder(HTTP_TRANSPORT, JSON_FACTORY, HttpRequestInitializer {
            it.apply {
                connectTimeout = 180_000
                readTimeout = 180_000
            }
            credential.initialize(it)
        })
                .setApplicationName(applicationName)
                .build()

    }

    /**
     * Create an editId and use it for read operations
     */
    private fun useEdit(publisher: AndroidPublisher, packageName: String, body: (edits: AndroidPublisher.Edits, editId: String) -> Unit) {
        val edits = publisher.edits()

        // Read or write operations need an editId
        val editRequest = edits.insert(packageName, null)
        val edit = editRequest.execute()
        val editId = edit.getId()
        Logger.i(String.format("Created edit with id: %s", editId))

        body.invoke(edits, editId)
    }

    /**
     * Create an editId and use it for read/write operations, finally commit edits
     */
    private fun makeEdit(publisher: AndroidPublisher, packageName: String, body: (edits: AndroidPublisher.Edits, editId: String) -> Unit) {
       useEdit(publisher, packageName){ edits, editId ->

           body.invoke(edits, editId)

           // Commit changes for edit.
           val commitRequest = edits.commit(packageName, editId)
           val appEdit = commitRequest.execute()
           Logger.i(String.format("App edit with id %s has been comitted", appEdit.getId()))
       }
    }

    /**
     * Upload an archive and assign it as a draft to a specific track
     */
    fun uploadDraft(googlePlayJson: String? = null,
                    packageName: String? = null,
                    track: GooglePlayTrack,
                    archiveFile: File): Long {
        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)
        val publisher = publisher(googlePlayJson, packageName_)
        var versionCode: Long = -1
        makeEdit(publisher, packageName_) { edits, editId ->
            // Upload new bundle to developer console
            val file = FileContent("application/octet-stream", archiveFile)
            versionCode = when (archiveFile.extension) {
                "aab" -> {
                    val uploadRequest = edits.bundles().upload(packageName_, editId, file)
                    Logger.i(String.format("Uploading bundle..."))
                    uploadRequest.execute().versionCode.toLong()
                }
                "apk" -> {
                    val uploadRequest = edits.apks().upload(packageName_, editId, file)
                    Logger.i(String.format("Uploading APK..."))
                    uploadRequest.execute().versionCode.toLong()
                }
                else -> {
                    throw IllegalArgumentException("The archive file provided is not supported (aab or apk authorized)")
                }
            }
            Logger.i(String.format("Version code %d has been uploaded", versionCode))

            val release = TrackRelease().apply {
                status = "draft"
                versionCodes = listOf(versionCode)
            }

            edits.tracks().update(packageName_, editId, track.name, Track().setReleases(listOf(release))).execute()
        }

        return versionCode
    }

    fun getListReleases(googlePlayJson: String? = null,
                        packageName: String? = null,
                        track: GooglePlayTrack): List<GooglePlayRelease>? {

        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)
        val publisher = publisher(googlePlayJson, packageName_)
        var listReleases: List<TrackRelease>? = null
        useEdit(publisher, packageName_) { edits, editId ->
            val releasesRequest = edits.tracks().get(packageName_, editId, track.name)
            listReleases = releasesRequest.execute().releases
        }
        return listReleases?.map { GooglePlayRelease(versionCodes = it.versionCodes, name = it.name, status = it.status) }
    }

    fun createRelease(
            googlePlayJson: String? = null,
            packageName: String? = null,
            listVersionCodes: List<Long>,
            track: GooglePlayTrack,
            releaseName: String? = null,
            percent: Double = 100.0,
            updatePriority: Int? = null) {
        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)

        val publisher = publisher(googlePlayJson, packageName_)
        makeEdit(publisher, packageName_) { edits, editId ->
            val release = TrackRelease().apply {
                name = releaseName ?: listVersionCodes.maxOrNull().toString()
                versionCodes = listVersionCodes
                updatePriority?.let { inAppUpdatePriority = it }
                if (percent == 100.0) {
                    status = "completed"
                } else {
                    userFraction = percent / 100.0
                    status = "inProgress"
                }
            }

            val updateTrackRequest = edits.tracks().update(packageName_, editId, track.name,
                    Track().setReleases(listOf(release)))
            Logger.i("Creating release (name=${release.name}, versionCodes=$listVersionCodes, userFraction=$percent) on track $track")
            updateTrackRequest.execute()
            Logger.i("Release ${release.name} created on track $track ")
        }
    }

    fun uploadWhatsNew(
            googlePlayJson: String? = null,
            packageName: String? = null,
            versionCode: Long,
            whatsNewProvider: (lang: String) -> String?) {
        Logger.i("uploading changelog for version $versionCode")

        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)

        val publisher = publisher(googlePlayJson, packageName_)

        makeEdit(publisher, packageName_) { edits, editId ->

            val list = edits.listings().list(packageName_, editId).execute()

            if (list.listings == null) {
                throw Exception("no listings found ?")
                // TODO: create a listing
            }

            val listLocalText = mutableListOf<LocalizedText>()
            for (listing in list.listings) {
                val content = whatsNewProvider(listing.language) ?: continue
                listLocalText.add(LocalizedText().setLanguage(listing.language).setText(content))
                Logger.i("Set changelog for ${listing.language} to $content")
            }

            /**
             * Update any release of any track where versionCodes.maxOrNull() match the versionCode param
             */
            edits.tracks().list(packageName_, editId).execute().tracks.forEach { track ->
                track.releases.filter { it.versionCodes.maxOrNull() == versionCode }.forEach { release ->
                    edits.tracks().update(packageName_, editId, track.track, Track().setReleases(listOf(release.clone().setReleaseNotes(listLocalText)))).execute()
                }
                Logger.i("Releases notes updated for trackRelease $versionCode on track beta")
            }
        }
    }

    data class ListingResource(
            val language: String,
            val title: String?,
            val shortDescription: String?,
            val description: String?,
            val video: String? = null
    )

    fun uploadListing(
            googlePlayJson: String? = null,
            packageName: String? = null,
            resources: List<ListingResource>
    ) {
        Logger.i("uploading listing")

        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)

        val publisher = publisher(googlePlayJson, packageName_)

        makeEdit(publisher, packageName_) { edits, editId ->
            for (resource in resources) {
                Logger.i("Set listing for ${resource.language}")
                Logger.i("   title: ${resource.title}")
                Logger.i("   shortDescription: ${resource.shortDescription}")
                Logger.i("   description: ${resource.description}")
                Logger.i("   video: ${resource.video}")

                val listing = Listing().apply {
                    language = resource.language
                    resource.title?.let { title = it }
                    resource.shortDescription?.let { shortDescription = it }
                    resource.description?.let { fullDescription = it }
                    resource.video?.let { video = it }
                }

                edits.listings()
                        .update(packageName_, editId, listing.language, listing)
                        .execute()
                Logger.i("Play store listing updated")
            }
        }
    }

    fun removeListings(
            googlePlayJson: String? = null,
            packageName: String? = null,
            languagesList: List<String>
    ) {
        Logger.i("uploading listing")

        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)

        val publisher = publisher(googlePlayJson, packageName_)

        makeEdit(publisher, packageName_) { edits, editId ->
            val existingListingsToRemove = edits.listings().list(packageName_, editId).execute().listings
                    .filter {
                        languagesList.contains(it.language)
                    }

            for (listing in existingListingsToRemove) {
                Logger.i("Removing language ${listing.language}")

                edits.listings()
                        .delete(packageName_, editId, listing.language)
                        .execute()
                Logger.i("Play store listing updated")
            }
        }
    }

    fun getListings(
            googlePlayJson: String? = null,
            packageName: String? = null
    ): List<ListingResource> {
        println("Getting listings from Google Play. Please wait.")
        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)
        val publisher = publisher(googlePlayJson, packageName_)
        val resources = mutableListOf<ListingResource>()

        useEdit(publisher, packageName_) { edits, editId ->
            resources.addAll(edits.listings().list(packageName_, editId).execute().listings.map {
                ListingResource(it.language, it.title, it.shortDescription, it.fullDescription, it.video)
            })
        }
        return resources
    }

    data class ImageUploadData(
            val file: File,
            val languageCode: String,
            val imageType: ImageType
    )

    data class ChangelogResource(
            val language: String,
            val description: String,
            val versionCode: Long
    )

    fun getChangeLogs(
            googlePlayJson: String? = null,
            packageName: String? = null
    ): List<ChangelogResource> {
        println("Getting changelogs from Google Play. Please wait.")
        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)
        val publisher = publisher(googlePlayJson, packageName_)
        val resources = mutableListOf<ChangelogResource>()

        useEdit(publisher, packageName_) { edits, editId ->
            resources.addAll(edits.tracks().list(packageName_, editId).execute().tracks.flatMap { track ->
                track.releases.flatMap { release ->
                    release.releaseNotes?.mapNotNull {
                        ChangelogResource(it.language, it.text, release.versionCodes.maxOrNull()!!)
                    } ?: listOf()
                }
            })
        }
        return resources
    }

    fun getPreviews(
            googlePlayJson: String? = null,
            packageName: String? = null
    ): List<PreviewImageData> {

        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)
        val publisher = publisher(googlePlayJson, packageName_)
        val resources = mutableListOf<PreviewImageData>()

        useEdit(publisher, packageName_) { edits, editId ->
            //Retrieve supported languages
            edits.listings().list(packageName_, editId).execute().listings.map { it.language }.map { lang ->
                //Cover any imageType
                ImageType.values().map { imageType ->
                    resources.addAll(edits.images()?.list(packageName_, editId, lang, imageType.value)?.execute()?.images?.map {
                        PreviewImageData(FilenameUtils.getName(it.url), it.url, lang, imageType)
                    } ?: listOf())
                }
            }
        }
        return resources
    }

    enum class ImageType(val value: String) {
        IMAGETYPE_FEATURE("featureGraphic"),
        IMAGETYPE_ICON("icon"),
        IMAGETYPE_PHONE("phoneScreenshots"),
        IMAGETYPE_SEVENINCH("sevenInchScreenshots"),
        IMAGETYPE_TENINCH("tenInchScreenshots"),
        IMAGETYPE_TVBANNER("tvBanner"),
        IMAGETYPE_TV("tvScreenshots"),
        IMAGETYPE_WEAR("wearScreenshots")
    }

    data class PreviewImageData(
            val id: String,
            val url: String,
            val languageCode: String,
            val imageType: ImageType
    )

    private data class GroupingKey(
            val imageType: ImageType,
            val languageCode: String
    )

    fun uploadImages(
            googlePlayJson: String? = null,
            packageName: String? = null,
            languageCode: String,
            imageType: ImageType,
            images: List<File>,
            overwrite: Boolean = false
    ) {
        Logger.i("uploading image")

        val packageName_ = packageName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)

        val publisher = publisher(googlePlayJson, packageName_)


        makeEdit(publisher, packageName_) { edits, editId ->

            if (overwrite) {
                //Delete all images matching imageType and language
                edits.images().deleteall(packageName_, editId, languageCode, imageType.value).execute()
            }

            //Upload images
            images.forEach { file ->

                val mimetype = when (file.extension.toLowerCase()) {
                    "png" -> "image/png"
                    "jpg" -> "image/jpeg"
                    "jpeg" -> "image/jpeg"
                    else -> {
                        throw IllegalArgumentException("Only jpg, jpeg and png extension are allowed (${file.absolutePath}")
                    }
                }
                edits.images().upload(
                        packageName_,
                        editId,
                        languageCode,
                        imageType.value,
                        object : AbstractInputStreamContent(mimetype) {
                            override fun getLength(): Long {
                                return file.length()
                            }

                            override fun retrySupported(): Boolean {
                                return true
                            }

                            override fun getInputStream(): InputStream {
                                return file.inputStream()
                            }

                        }).execute()
            }
        }
    }

    fun isConfigured() =
            KintaEnv.get(KintaEnv.Var.GOOGLE_PLAY_JSON)?.isNotBlank() == true
                    && KintaEnv.get(KintaEnv.Var.GOOGLE_PLAY_PACKAGE_NAME)?.isNotBlank() == true
}
