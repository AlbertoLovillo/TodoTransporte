package com.s25am.todotransporte.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.s25am.todotransporte.ui.screens.authentication.LoginScreen
import com.s25am.todotransporte.ui.screens.authentication.RegisterScreen
import com.s25am.todotransporte.ui.screens.maps.MapsScreen
import com.s25am.todotransporte.ui.screens.schedule.ScheduleScreen
import com.s25am.todotransporte.ui.screens.wallet.WalletScreen

/**
 * Gestor de navegación principal de la aplicación.
 * Define qué pantalla se muestra en cada momento.
 */
@Composable
fun AppNavigation(
    padding: PaddingValues,
    backStack: NavBackStack<NavKey>
) {

    Box(modifier = Modifier.padding(padding)) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {

                entry<Routes.Login> {
                    LoginScreen(
                        onNavigateToRegister = { backStack.add(Routes.Register) },
                        onLoginSuccess = {
                            while (backStack.isNotEmpty()) {
                                backStack.removeLastOrNull()
                            }
                            backStack.add(Routes.Maps)
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
                            backStack.add(Routes.Maps)
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
            }
        )
    }
}