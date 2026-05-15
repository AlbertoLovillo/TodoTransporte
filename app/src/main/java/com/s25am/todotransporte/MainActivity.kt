package com.s25am.todotransporte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.s25am.todotransporte.navigation.AppNavigation
import com.s25am.todotransporte.navigation.Routes
import com.s25am.todotransporte.ui.components.BarraNavegacion
import com.s25am.todotransporte.ui.components.BarraSuperior
import com.s25am.todotransporte.ui.screens.tickets.TicketsViewModel
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTransporteTheme {

                val walletViewModel: TicketsViewModel = viewModel()
                val walletUiState by walletViewModel.uiState.collectAsState()

                val backStack = rememberNavBackStack(Routes.SplashScreen)
                val currentRoute = backStack.lastOrNull()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        BarraSuperior(
                            currentRoute = currentRoute,
                            canNavigateBack = backStack.size > 1,
                            saldo = walletUiState.saldo,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    },
                    bottomBar = {
                        BarraNavegacion(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                if (currentRoute != route) {
                                    (backStack as androidx.navigation3.runtime.NavBackStack<Any>).add(route)
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    AppNavigation(
                        padding = innerPadding,
                        backStack = backStack,
                        ticketsViewModel = walletViewModel
                    )
                }
            }
        }
    }
}