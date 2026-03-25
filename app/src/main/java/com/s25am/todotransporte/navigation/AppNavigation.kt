package com.s25am.todotransporte.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.s25am.todotransporte.ui.screens.HomeScreen
import com.s25am.todotransporte.ui.screens.MapsScreen
import com.s25am.todotransporte.ui.screens.ScheduleScreen
import com.s25am.todotransporte.ui.screens.WalletScreen

@Composable
fun AppNavigation(
    padding: PaddingValues
) {
    val backStack = rememberNavBackStack(Routes.Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Routes.Home> {
                HomeScreen()
            }
            entry<Routes.Maps> {
                MapsScreen()
            }
            entry<Routes.Schedule> {
                ScheduleScreen()
            }
            entry<Routes.Wallet> {
                WalletScreen()
            }
        }
    )
}