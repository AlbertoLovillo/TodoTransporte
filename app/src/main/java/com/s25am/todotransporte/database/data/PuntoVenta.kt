package com.s25am.todotransporte.database.data

import kotlinx.serialization.Serializable

@Serializable
data class PuntoVenta(
    val id: Int,
    val serial_emt: Int? = null,
    val domicilio: String? = null,
    val tipo: String? = null,
    val latitud: Double,
    val longitud: Double
)
