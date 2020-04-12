package com.dailymotion.kinta.integration.github

object GithubActions {
    fun isTag(): Boolean {
        val ref = System.getenv("GITHUB_REF")
        return ref?.startsWith("refs/tags/") == true
    }

    fun tagName(): String {
        val ref = System.getenv("GITHUB_REF")

        check(isTag()) {
            "$ref is not a tag."
        }

        return ref!!.substringAfter("refs/tags/")
    }

    fun isMaster(): Boolean {
        val eventName = System.getenv("GITHUB_EVENT_NAME")
        val ref = System.getenv("GITHUB_REF")

        return eventName == "push" && ref == "refs/heads/master"
    }
}