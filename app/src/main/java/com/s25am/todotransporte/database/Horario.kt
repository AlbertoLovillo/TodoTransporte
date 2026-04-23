package com.s25am.todotransporte.database

import kotlinx.serialization.Serializable

@Serializable
data class Horario(
    val hora_llegada: String
)