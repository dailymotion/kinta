package com.dailymotion.kinta.integration.gcp

import com.dailymotion.kinta.KintaEnv
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.datastore.*

object CloudDatastore {

    fun store(cloudStorageJson: String?): Datastore {
        val cloudStorageJson_ = cloudStorageJson ?: KintaEnv.getOrFail(KintaEnv.Var.GOOGLE_CLOUD_DATASTORE_JSON)

        val credentials = GoogleCredentials.fromStream(cloudStorageJson_.byteInputStream())
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

        val storeBuilder = DatastoreOptions
                .newBuilder()
                .setCredentials(credentials)

        // Force the correct use of the Project ID: just in case the wrong projectId is loaded
        val projectId = (credentials as? ServiceAccountCredentials?)?.projectId
        projectId?.let {
            storeBuilder.setProjectId(it)
        }

        return storeBuilder.build().service!!
    }
}