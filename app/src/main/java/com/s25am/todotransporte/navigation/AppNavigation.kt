package com.s25am.todotransporte.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.s25am.todotransporte.ui.components.AnimatedSplashScreen
import com.s25am.todotransporte.ui.screens.authentication.LoginScreen
import com.s25am.todotransporte.ui.screens.authentication.RegisterScreen
import com.s25am.todotransporte.ui.screens.bus_map.BusMapScreen
import com.s25am.todotransporte.ui.screens.sale_point.SalePointScreen
import com.s25am.todotransporte.ui.screens.schedule.ScheduleScreen
import com.s25am.todotransporte.ui.screens.tickets.TicketsViewModel
import com.s25am.todotransporte.ui.screens.tickets.shop.ShopScreen
import com.s25am.todotransporte.ui.screens.tickets.wallet.WalletScreen


@Composable
fun AppNavigation(
    padding: PaddingValues,
    backStack: NavBackStack<NavKey>,
    ticketsViewModel: TicketsViewModel
) {

    Box(modifier = Modifier.padding(padding)) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {

                entry<Routes.SplashScreen> {
                    AnimatedSplashScreen(onNavigationNext = {
                        while (backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                        backStack.add(Routes.Login)
                    })
                }

                entry<Routes.Login> {
                    LoginScreen(
                        onNavigateToRegister = { backStack.add(Routes.Register) },
                        onLoginSuccess = {
                            while (backStack.isNotEmpty()) { backStack.removeLastOrNull() }
                            backStack.add(Routes.BusMap)
                        }
                    )
                }

                entry<Routes.Register> {
                    RegisterScreen(
                        onNavigateToLogin = {
                            backStack.removeLastOrNull()
                            backStack.add(Routes.Login)
                        },
                        onRegisterSuccess = {
                            while (backStack.isNotEmpty()) {
                                backStack.removeLastOrNull()
                            }
                            backStack.add(Routes.BusMap)
                        }
                    )
                }

                entry<Routes.BusMap> {
                    BusMapScreen(ticketsViewModel = ticketsViewModel)
                }

                entry<Routes.Schedule> {
                    ScheduleScreen()
                }

                entry<Routes.Wallet> {
                    WalletScreen(viewModel = ticketsViewModel)
                }
                entry<Routes.SalePoint> {
                    SalePointScreen()
                }
                entry<Routes.Shop> {
                    ShopScreen(
                        viewModel = ticketsViewModel,
                        onBack = { backStack.add(Routes.Wallet) },
                        onNavigateToMaps = {backStack.add(Routes.BusMap)}
                    )
                }
            }
        )
    }
}