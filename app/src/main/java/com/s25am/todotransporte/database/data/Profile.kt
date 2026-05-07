package com.s25am.todotransporte.database.data

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val email: String? = null,
    val nombre: String? = null,
    val balance: Double = 0.0
)
