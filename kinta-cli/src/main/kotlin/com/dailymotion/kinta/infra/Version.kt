package com.dailymotion.kinta.infra

class Version(val value: String): Comparable<Version> {
    override fun compareTo(other: Version): Int {
        val t = value.split(".").map {
            it.toInt()
        }
        val o = value.split(".").map {
            it.toInt()
        }

        for (i in 0 until t.size - 1) {
            val diff = t.get(i) - (o.getOrNull(i) ?: 0)
            if (diff != 0) {
                return diff
            }
        }

        return 0
    }
}
