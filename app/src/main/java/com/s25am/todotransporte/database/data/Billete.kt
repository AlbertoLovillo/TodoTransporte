package com.s25am.todotransporte.database.data

import kotlinx.serialization.Serializable

@Serializable
data class Billete(
    val id: String = "",
    val titulo: String = "",
    val trayecto: String = "",
    val fecha: String = "",
    val precio: String = "",
    val email_usuario: String = ""
)