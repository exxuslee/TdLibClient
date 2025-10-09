package org.exxuslee.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ArkhamMessage(
    val from: String,
    val to: String,
    val value: String,
    val network: String,
    val timestamp: Long,
)