package com.s25am.todotransporte.database.data

import kotlinx.serialization.Serializable

/**
 * Modelo de datos para representar un billete o abono de transporte.
 */
@Serializable
data class Ticket(
    val id: String,
    val type: String,
    val description: String,
    val price: Double
)
