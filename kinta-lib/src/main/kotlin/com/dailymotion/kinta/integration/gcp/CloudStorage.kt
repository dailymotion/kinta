package com.dailymotion.kinta.integration.gcp

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.*
import java.io.FileInputStream
import java.nio.file.Paths


object CloudStorage {


    private fun bucket(cloudStorageJson: String?, bucketName: String?): Bucket {
        val cloudStorageJson_ = cloudStorageJson ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_CLOUD_STORAGE_JSON)

        val bucket_ = bucketName ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_CLOUD_STORAGE_BUCKET)

        val credentials = GoogleCredentials.fromStream(cloudStorageJson_.byteInputStream())
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
        val service = StorageOptions.newBuilder().setCredentials(credentials).build().service

        return service.get(bucket_)!!
    }

    fun deleteRecursively(
            cloudStorageJson: String? = null,
            bucket: String? = null,
            name: String,
            verbose: Boolean = false) {
        bucket(cloudStorageJson, bucket).list(Storage.BlobListOption.prefix(name)).values.forEach {
            if (verbose) {
                Logger.i("deleting ${it.name}")
            }
            it.delete()
        }
    }

    fun create(
            cloudStorageJson: String? = null,
            bucket: String? = null,
            remotePath: String,
            inputStream: FileInputStream) {
        bucket(cloudStorageJson, bucket).create(remotePath, inputStream)
    }

    fun download(cloudStorageJson: String? = null,
                 bucket: String? = null,
                 blobName: String,
                 destFilePath: String) {
        bucket(cloudStorageJson, bucket)
                .get(blobName)
                .downloadTo(Paths.get(destFilePath))
    }
}