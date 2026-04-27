package com.s25am.todotransporte.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.s25am.todotransporte.R
import com.s25am.todotransporte.navigation.Routes
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

@Composable
fun MainNavigationBar(
    currentRoute: Any?,
    onNavigate: (Any) -> Unit
) {

    // Solo mostramos la barra si NO estamos en Login o Registro
    if (currentRoute != Routes.Login && currentRoute != Routes.Register) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = colorResource(id = R.color.RojoP)
        ) {
            // Ítem: MAPA
            NavigationBarItem(
                selected = currentRoute == Routes.Maps,
                onClick = { onNavigate(Routes.Maps) },
                icon = { Icon(Icons.Default.Place, contentDescription = "Mapa") },
                label = { Text("Mapa") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.RojoP),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.2f)
                )
            )

            // Ítem: HORARIOS
            NavigationBarItem(
                selected = currentRoute == Routes.Schedule,
                onClick = { onNavigate(Routes.Schedule) },
                icon = { Icon(Icons.Default.DateRange, contentDescription = "Horarios") },
                label = { Text("Horarios") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.RojoP),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.2f)
                )
            )

            // Ítem: CARTERA
            NavigationBarItem(
                selected = currentRoute == Routes.Wallet,
                onClick = { onNavigate(Routes.Wallet) },
                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Cartera") },
                label = { Text("Cartera") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.RojoP),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Vista previa de la barra")
@Composable
fun MainNavigationBarPreview() {
    TodoTransporteTheme {
        // El Scaffold es necesario para que la barra se posicione abajo
        Scaffold(
            bottomBar = {
                // Le pasamos Maps para ver cómo queda seleccionada
                MainNavigationBar(
                    currentRoute = Routes.Maps,
                    onNavigate = {}
                )
            }
        ) { innerPadding ->
            // IMPORTANTE: Aquí usamos el padding para que el contenido
            // no se solape con la barra
            Box(modifier = Modifier.padding(innerPadding)) {
                // Texto de relleno para el preview
                Text(text = "El contenido de la pantalla va aquí")
            }
        }
    }
}