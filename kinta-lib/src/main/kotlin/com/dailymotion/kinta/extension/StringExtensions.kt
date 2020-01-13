package com.dailymotion.kinta.extension

fun String.toSlug(): String {
    val builder = StringBuilder()
    for (c in this) {
        when {
            c in 'a'..'z'
                    || c in '0'..'9'
                    || c == '-'
            -> builder.append(c)
            c in 'A'..'Z'
            -> builder.append(c.toLowerCase())
            c == ' '
            -> builder.append('_')
        }
    }
    return builder.toString()
}