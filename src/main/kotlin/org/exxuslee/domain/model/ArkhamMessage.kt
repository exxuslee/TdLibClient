package org.exxuslee.domain.model

data class ArkhamMessage(
    val from: String,
    val to: String,
    val value: String,
    val network: String
)