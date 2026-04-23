package com.s25am.todotransporte.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RespuestaParada(
    @SerialName("Parada")
    val parada: Parada?
)
