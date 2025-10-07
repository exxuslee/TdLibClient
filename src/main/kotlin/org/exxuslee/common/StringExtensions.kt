package org.exxuslee.common

fun String.removePrefixFromLink(): String =
    "@" + this
        .removePrefix("https://t.me/")
        .removePrefix("t.me/")
        .removePrefix("@")
        .trim()



