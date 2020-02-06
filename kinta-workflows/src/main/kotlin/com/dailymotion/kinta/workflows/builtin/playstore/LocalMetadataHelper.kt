package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.integration.googleplay.GooglePlayIntegration
import java.io.*


object LocalMetadataHelper {

    val ANDROID_METADATA_FOLDER = File("kintaSrc/metadata/android/")

    private fun checkMetaDataFolder() {
        check(ANDROID_METADATA_FOLDER.exists()) {
            "$ANDROID_METADATA_FOLDER not found. Make sure to call 'kinta ${GetPlayStoreMetadata.commandName}' first or create the files tree manually"
        }
    }

    /**
     * Retrieve local listings metadatas
     */
    fun getAllListing(): List<GooglePlayIntegration.ListingResource> {
        checkMetaDataFolder()
        return ANDROID_METADATA_FOLDER.listFiles()?.filter { it.isDirectory }?.map { languageFolder ->
            GooglePlayIntegration.ListingResource(
                    language = languageFolder.name,
                    title = getStringFromFile(File(languageFolder, "title.txt")),
                    shortDescription = getStringFromFile(File(languageFolder, "short_description.txt")),
                    description = getStringFromFile(File(languageFolder, "full_description.txt")),
                    video = getStringFromFile(File(languageFolder, "video.txt"))
            )
        } ?: listOf()
    }

    /**
     * Retrieve all localized changeLogs for a specific version code
     * @param versionCode
     */
    fun getChangelog(versionCode: Long): List<GooglePlayIntegration.ChangelogResource> {
        checkMetaDataFolder()
        return ANDROID_METADATA_FOLDER.listFiles()?.filter { it.isDirectory }
                ?.mapNotNull { languageFolder ->
                    val changelogsFolder = languageFolder.listFiles()?.find { it.name == "changelogs" }?.listFiles()
                    /**
                     * Find the {VERSION_CODE}.txt or default.txt file.
                     */
                    val changelogFile = changelogsFolder?.find { it.name == "$versionCode.txt" }
                            ?: changelogsFolder?.find { it.name == "default.txt" }

                    if (changelogFile != null) {
                        GooglePlayIntegration.ChangelogResource(
                                language = languageFolder.name,
                                description = getStringFromFile(changelogFile) ?: "",
                                versionCode = versionCode
                        )
                    } else {
                        null
                    }

                } ?: listOf()
    }

    /**
     * Retrieve local images metadata
     * @param languageCode optional filter
     * @param imageType optional filter
     */
    fun getImages(languageCode: String? = null, imageType: String? = null): List<GooglePlayIntegration.ImageUploadData> {
        checkMetaDataFolder()
        val listResources = mutableListOf<GooglePlayIntegration.ImageUploadData>()
        ANDROID_METADATA_FOLDER.listFiles()
                ?.filter { it.name == languageCode || languageCode == null }
                ?.map { localeFolder ->
                    File(localeFolder, "images").listFiles()
                            ?.filter { it.name == imageType || imageType == null }
                            ?.forEach { imageTypeFolder ->
                                imageTypeFolder.listFiles()?.forEach {
                                    listResources.add(GooglePlayIntegration.ImageUploadData(
                                            file = it,
                                            imageType = GooglePlayIntegration.ImageType.values().find { it.value == imageTypeFolder.name }!!,
                                            languageCode = localeFolder.name
                                    ))
                                }
                            }
                }
        return listResources
    }

    private fun isDirectoryEmpty(file: File): Boolean {
        if (!file.isDirectory) {
            return false
        }
        return file.listFiles()?.find { !isDirectoryEmpty(it) } == null
    }

    fun cleanMetaDatas() {
        ANDROID_METADATA_FOLDER.listFiles()?.filter { it.isDirectory }?.map { languageFolder ->
            languageFolder.listFiles()?.filter { it.name != "images" }?.forEach {
                //Remove all except images directory
                it.deleteRecursively()
            }

            //Remove language directory if empty or contains empty folder
            if (isDirectoryEmpty(languageFolder)) {
                languageFolder.delete()
            }
        }
    }

    fun saveListings(listings: List<GooglePlayIntegration.ListingResource>) {
        listings.groupBy { it.language }.map {
            println("Saving listings language : ${it.key}")
            val folder = File(ANDROID_METADATA_FOLDER, it.key).apply {
                mkdirs()
            }
            it.value.forEach {
                it.title?.let { writeTextFile(File(folder, "title.txt"), it) }
                it.shortDescription?.let { writeTextFile(File(folder, "short_description.txt"), it) }
                it.description?.let { writeTextFile(File(folder, "full_description.txt"), it) }
                it.video?.let { writeTextFile(File(folder, "video.txt"), it) }
            }
        }
    }

    fun saveChangeLogs(changeLogs: List<GooglePlayIntegration.ChangelogResource>) {
        changeLogs.groupBy { it.language }.map {
            println("Saving changeLogs language : ${it.key}")
            val folder = File(ANDROID_METADATA_FOLDER, "${it.key}/changelogs").apply {
                mkdirs()
            }
            it.value.forEach {
                writeTextFile(File(folder, "${it.versionCode}.txt"), it.description)
            }
        }
    }

    private fun convertStreamToString(inputStream: InputStream?): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line).append("\n")
        }
        reader.close()
        return sb.toString().trim()
    }

    private fun getStringFromFile(file: File): String? {
        val fin = FileInputStream(file)
        return try {
            convertStreamToString(fin)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            fin.close()
        }
    }

    private fun writeTextFile(file: File, text: String) {
        val writer = PrintWriter(file, "UTF-8")
        writer.println(text.trim())
        writer.close()
    }
}