package com.s25am.todotransporte.database

import kotlinx.serialization.Serializable

@Serializable
data class Parada(
    val id: Int,
    val nombre: String,
    val latitud: Double,
    val longitud: Double
)

