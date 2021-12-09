package com.dailymotion.kinta

object KintaEnv {

    enum class Var {
        GOOGLE_PLAY_PACKAGE_NAME,
        GOOGLE_CLOUD_STORAGE_BUCKET,
        APPCENTER_ORGANIZATION,
        SLACK_WEBHOOK_URL,
        GITLAB_PERSONAL_TOKEN,
        GITHUB_TOKEN,
        GITHUB_USERNAME,
        APPCENTER_TOKEN,
        GITHUB_APP_CLIENT_ID,
        GITHUB_APP_CLIENT_SECRET,
        TRANSIFEX_USER,
        TRANSIFEX_PASSWORD,
        TRANSIFEX_PROJECT,
        GOOGLE_PLAY_JSON,
        GOOGLE_CLOUD_STORAGE_JSON,
        GOOGLE_CLOUD_DATASTORE_JSON,
        JIRA_USERNAME,
        JIRA_PASSWORD,
        BITRISE_PERSONAL_TOKEN,
        JIRA_URL,
        APPLE_PASSWORD,
        APPLE_USERNAME,
        KINTA_KEYSTORE,
        KINTA_KEYSTORE_PASSWORD,
        KINTA_KEY_ALIAS,
        KINTA_KEY_PASSWORD,
        REPOSITORY_NAME,
        APPGALLERY_CLIENT_ID,
        APPGALLERY_CLIENT_SECRET,
        APPGALLERY_PACKAGE_NAME,
        APPGALLERY_TOKEN,
    }

    fun get(variable: Var): String? = get(variable.name)

    fun get(key: String): String? {
        val local = EnvProperties.get(key)
        if (local != null) {
            return local
        }
        return System.getenv(key)
    }

    fun getOrFail(variable: Var) = getOrFail(variable.name)

    fun getOrFail(key: String): String {
        val v = get(key)
        check(!v.isNullOrBlank()) {
            "Cannot find $key, please set it in your environment or pass it explicitly."
        }

        return v
    }

    fun put(variable: Var, value: String?) = put(variable.name, value)

    fun put(key: String, value: String?) {
        EnvProperties.put(key, value)
    }

    fun updateAvailableBuiltInEnvs(){
        EnvProperties.updateAvailableBuiltInEnvs(Var.values().map { it.name })
    }
}