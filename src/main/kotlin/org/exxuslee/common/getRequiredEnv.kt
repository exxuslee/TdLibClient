package org.exxuslee.common

import io.github.cdimascio.dotenv.Dotenv

fun getRequiredEnv(key: String, dotenv: Dotenv): String {
        val fromSystem = System.getenv(key)
        val value = fromSystem ?: dotenv.get(key)
        return value ?: throw IllegalStateException(
            "Missing required environment variable '$key'. Create a .env file (or set env var) with $key."
        )
    }