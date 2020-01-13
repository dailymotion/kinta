package com.dailymotion.kinta

import com.github.ajalt.clikt.core.CliktCommand

interface Workflows {
    fun all(): List<CliktCommand>
}