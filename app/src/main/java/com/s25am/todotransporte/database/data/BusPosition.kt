package com.s25am.todotransporte.database.data

import kotlinx.serialization.Serializable

@Serializable
data class BusPosition(
    val codBus: String,
    val codLinea: String,
    val sentido: Int,
    val lon: Double,
    val lat: Double,
    val lastUpdate: String
)
