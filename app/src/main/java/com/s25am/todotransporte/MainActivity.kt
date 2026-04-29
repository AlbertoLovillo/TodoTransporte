package com.s25am.todotransporte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.rememberNavBackStack
import com.s25am.todotransporte.navigation.AppNavigation
import com.s25am.todotransporte.navigation.Routes
import com.s25am.todotransporte.ui.components.MainNavigationBar
import com.s25am.todotransporte.ui.components.MainTopBar
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTransporteTheme {
                val backStack = rememberNavBackStack(Routes.Login)
                val currentRoute = backStack.lastOrNull()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    topBar = {
                        MainTopBar(
                            currentRoute = currentRoute,
                            canNavigateBack = backStack.size > 1,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    },
                    bottomBar = {
                        MainNavigationBar(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                if (currentRoute != route) {
                                    // TODO: Forzamos al backStack a aceptar el objeto directamente Esto hay que revisarlo errores raros
                                    (backStack as androidx.navigation3.runtime.NavBackStack<Any>).add(route)
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    AppNavigation(padding = innerPadding,
                    backStack = backStack
                    )
                }
            }
        }
    }
}