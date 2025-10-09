package org.exxuslee.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalData(
    val posts: List<ArkhamMessage>,
)