package com.s25am.todotransporte.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes : NavKey {
    @Serializable
    data object Login : Routes()

    @Serializable
    data object Register : Routes()

    @Serializable
    data object MapaBus : Routes()

    @Serializable
    data object Horario : Routes()

    @Serializable
    data object Cartera : Routes()
    @Serializable
    data object PuntosVenta : Routes()
    @Serializable
    data object Tienda : Routes()
    @Serializable
    data object SplashScreen: Routes()
}