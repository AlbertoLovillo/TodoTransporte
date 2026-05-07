package com.s25am.todotransporte.database.data

import kotlinx.serialization.Serializable

@Serializable
data class RutaGeometria(
    val id_linea: Int,
    val direccion: Int,
    val geojson: String
)