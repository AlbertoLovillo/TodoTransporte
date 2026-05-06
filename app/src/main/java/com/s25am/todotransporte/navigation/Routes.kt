package com.s25am.todotransporte.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes : NavKey {
    @Serializable
    data object Login : Routes()

    @Serializable
    data object Register : Routes()

    @Serializable
    data object Maps : Routes()

    @Serializable
    data object Schedule : Routes()

    @Serializable
    data object Wallet : Routes()
    @Serializable
    data object SalePoint : Routes()
    @Serializable
    data object ByTickets : Routes()
}