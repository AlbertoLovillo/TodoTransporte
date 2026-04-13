package com.s25am.todotransporte.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()
    @Serializable
    data object Maps : Routes()
    @Serializable
    data object Schedule : Routes()
    @Serializable
    data object Wallet : Routes()
}