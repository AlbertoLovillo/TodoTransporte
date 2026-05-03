package com.s25am.todotransporte.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.s25am.todotransporte.ui.screens.authentication.LoginScreen
import com.s25am.todotransporte.ui.screens.authentication.RegisterScreen
import com.s25am.todotransporte.ui.screens.maps.MapsScreen
import com.s25am.todotransporte.ui.screens.schedule.ScheduleScreen
import com.s25am.todotransporte.ui.screens.wallet.BuyTicketScreen
import com.s25am.todotransporte.ui.screens.wallet.WalletScreen

/**
 * Gestor de navegación principal de la aplicación.
 * Define qué pantalla se muestra en cada momento.
 */
@Composable
fun AppNavigation(
    padding: PaddingValues,
    backStack: androidx.navigation3.runtime.NavBackStack<*>
) {
//    val backStack = rememberNavBackStack(Routes.Login)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = Modifier.padding(padding),
        entryProvider = entryProvider {

            entry<Routes.Login> {
                LoginScreen(
                    onNavigateToRegister = { (backStack as androidx.navigation3.runtime.NavBackStack<Any>).add(Routes.Register) },
                    onLoginSuccess = {
                        while(backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                        //Todos estos son forazados esto se debe a que lo que pasamos el parametro
                        // de backstack pero no lo reconoce como un objeto de navDispley osea ruta
                        // por lo que no sabe si lo que pasamos es una ruta o un objeto entonces lo forzamos para que lo use
                        (backStack as androidx.navigation3.runtime.NavBackStack<Any>).add(Routes.Maps)
                    }
                )
            }

            entry<Routes.Register> {
                RegisterScreen(
                    onNavigateToLogin = {
                        backStack.removeLastOrNull()
                    },
                    onRegisterSuccess = {
                        while (backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                        (backStack as androidx.navigation3.runtime.NavBackStack<Any>).add(Routes.Maps)
                    }
                )
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

            entry<Routes.ByTickets> {BuyTicketScreen(
                onBack = {
                    backStack.removeLastOrNull()
                }
            )
            }
        }
    )
}