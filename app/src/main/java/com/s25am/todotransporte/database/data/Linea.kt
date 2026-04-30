package com.s25am.todotransporte.database.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Linea(
    val id: Int,
    val codigo: String?,
    val nombre: String,
    val color: String,
    @SerialName("ruta_geojson")
    val rutaGeojson: String?
)