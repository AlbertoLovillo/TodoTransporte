package com.s25am.todotransporte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.s25am.todotransporte.navigation.AppNavigation
import com.s25am.todotransporte.ui.components.MainNavigationBar
import com.s25am.todotransporte.ui.components.MainTopBar
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTransporteTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        MainTopBar()
                    },
                    bottomBar = {
                        MainNavigationBar()
                    }
                ) { innerPadding ->
                    AppNavigation(padding = innerPadding)
                }
            }
        }
    }
}