package com.s25am.todotransporte.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.s25am.todotransporte.ui.screens.authentication.LoginScreen
import com.s25am.todotransporte.ui.screens.authentication.RegisterScreen
import com.s25am.todotransporte.ui.screens.authentication.viewmodel.AuthenticationViewModel
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
) {
    // Definimos la pila de navegación empezando por la pantalla de Login
    val backStack = rememberNavBackStack(Routes.Login)

    // Instanciamos el ViewModel de autenticación para compartirlo entre Login y Registro
    val authViewModel: AuthenticationViewModel = viewModel()

    // Componente que visualiza las pantallas según el estado del backStack
    NavDisplay(
        backStack = backStack,
        // Acción que ocurre cuando el usuario pulsa el botón "atrás" del sistema
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            // --- 🔐 PANTALLA DE LOGIN ---
            entry<Routes.Login> {
                LoginScreen(
                    viewModel = authViewModel,
                    // Si pulsa registrarse, añadimos la pantalla de registro a la pila
                    onNavigateToRegister = { backStack.add(Routes.Register) },
                    onLoginSuccess = {
                        // Al entrar con éxito, vaciamos TODA la pila (Login, etc.)
                        // Esto evita que el usuario vuelva al Login al dar atrás desde el Mapa
                        while(backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                        // Añadimos el Mapa como única pantalla en la pila
                        backStack.add(Routes.Maps)
                    }
                )
            }

            // --- 📝 PANTALLA DE REGISTRO ---
            entry<Routes.Register> {
                RegisterScreen(
                    viewModel = authViewModel,
                    // Si decide volver, quitamos el Registro de la pila para ver el Login que estaba debajo
                    onNavigateToLogin = {
                        backStack.removeLastOrNull()
                    },
                    onRegisterSuccess = {
                        // Tras un registro exitoso, también limpiamos la pila y vamos al mapa
                        while (backStack.size > 0) {
                            backStack.removeLastOrNull()
                        }
                        backStack.add(Routes.Maps)
                    }
                )
            }

            // --- 🗺 PANTALLA PRINCIPAL (MAPA) ---
            entry<Routes.Maps> {
                MapsScreen()
            }

            // --- 📅 PANTALLA DE HORARIOS ---
            entry<Routes.Schedule> {
                ScheduleScreen()
            }

            // --- 💳 PANTALLA DE CARTERA/PAGOS ---
            entry<Routes.Wallet> {
                WalletScreen()
            }
        }
    )
}