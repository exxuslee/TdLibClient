package org.exxuslee.domain.model

sealed class Amount {
    data class Stable(val value: Long) : Amount()
    data class Coin(val value: Long) : Amount()
}