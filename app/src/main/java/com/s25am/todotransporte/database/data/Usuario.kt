package com.s25am.todotransporte.database.data

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String = "",
    val email: String?,
    val nombre: String? = "",
    val saldo: Double = 0.0
)
