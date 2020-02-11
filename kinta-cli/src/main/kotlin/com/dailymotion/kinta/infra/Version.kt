package com.dailymotion.kinta.infra

class Version(val value: String): Comparable<Version> {
    val isSnapshot: Boolean
    val major: Int
    val minor: Int
    val patch: Int


    init {
        val result = Regex("([0-9]*)\\.([0-9]*)\\.([0-9]*)([^0-9]*)").matchEntire(value)
        check(result != null) {
            "Cannot parse version: $value"
        }
        val snapshot = result.groupValues[4]
        check(snapshot.isBlank() || snapshot == "-SNAPSHOT") {
            "Unrecognized snapshot: $snapshot"
        }
        isSnapshot = snapshot == "-SNAPSHOT"
        major = result.groupValues[1].toInt()
        minor = result.groupValues[2].toInt()
        patch = result.groupValues[3].toInt()
    }

    override fun compareTo(other: Version): Int {

        if (this.major - other.major != 0) {
            return this.major - other.major
        }
        if (this.minor - other.minor != 0) {
            return this.minor - other.minor
        }
        if (this.patch - other.patch != 0) {
            return this.patch - other.patch
        }

        return this.isSnapshot.compareTo(other.isSnapshot)
    }
}
