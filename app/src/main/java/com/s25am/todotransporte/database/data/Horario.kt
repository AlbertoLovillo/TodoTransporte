package com.s25am.todotransporte.database.data

import kotlinx.serialization.Serializable

@Serializable
data class Horario(
    val id_linea: Int,
    val id_parada: Int,
    val hora_llegada: String,
    val destino: String? = null,
    val direccion: Int? = null
)