package com.dailymotion.kinta

object KintaEnv {
    const val GOOGLE_PLAY_PACKAGE_NAME = "GOOGLE_PLAY_PACKAGE_NAME"
    const val GOOGLE_CLOUD_STORAGE_BUCKET = "GOOGLE_CLOUD_STORAGE_BUCKET"
    const val APPCENTER_ORGANIZATION = "APPCENTER_ORGANIZATION"
    const val SLACK_WEBHOOK_URL = "SLACK_WEBHOOK_URL"
    const val GITLAB_PERSONAL_TOKEN = "GITLAB_PERSONAL_TOKEN"
    const val GITHUB_TOKEN = "GITHUB_TOKEN"
    const val GITHUB_USERNAME = "GITHUB_USERNAME"
    const val APPCENTER_TOKEN = "APPCENTER_TOKEN"
    const val CI_BACKEND_TOKEN = "CI_BACKEND_TOKEN"
    const val GITHUB_APP_CLIENT_ID = "GITHUB_APP_CLIENT_ID"
    const val GITHUB_APP_CLIENT_SECRET = "GITHUB_APP_CLIENT_SECRET"

    const val TRANSIFEX_USER = "TRANSIFEX_USER"
    const val TRANSIFEX_PASSWORD = "TRANSIFEX_PASSWORD"
    const val TRANSIFEX_PROJECT = "TRANSIFEX_PROJECT"

    const val GOOGLE_PLAY_JSON = "GOOGLE_PLAY_JSON"
    const val GOOGLE_CLOUD_STORAGE_JSON = "GOOGLE_CLOUD_STORAGE_JSON"
    const val JIRA_USERNAME = "JIRA_USERNAME"
    const val JIRA_PASSWORD = "JIRA_PASSWORD"
    const val BITRISE_PERSONAL_TOKEN = "BITRISE_PERSONAL_TOKEN"
    const val JIRA_URL = "JIRA_URL"

    fun get(key: String): String? {
        return System.getenv(key)
    }

    fun getOrFail(key: String): String {
        val v = System.getenv(key)
        check(!v.isNullOrBlank()) {
            "Cannot find $v, please set it in your environment or pass it explicitly."
        }

        return v
    }
}