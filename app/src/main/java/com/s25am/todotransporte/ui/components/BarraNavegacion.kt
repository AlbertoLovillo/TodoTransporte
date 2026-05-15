package com.s25am.todotransporte.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.navigation.Routes

@Composable
fun BarraNavegacion(
    currentRoute: Any?,
    onNavigate: (Any) -> Unit
) {

    if (currentRoute != Routes.Login && currentRoute != Routes.Register &&
        currentRoute != Routes.SplashScreen
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = colorResource(id = R.color.rojoPrincipal),
            modifier = Modifier.height(90.dp)
        ) {

            NavigationBarItem(
                selected = currentRoute == Routes.PuntosVenta,
                onClick = { onNavigate(Routes.PuntosVenta) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Puntos de Venta"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.rojoPrincipal),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.2f)
                )
            )

            NavigationBarItem(
                selected = currentRoute == Routes.Horario,
                onClick = { onNavigate(Routes.Horario) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Horarios"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.rojoPrincipal),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.2f)
                )
            )

            NavigationBarItem(
                selected = currentRoute == Routes.MapaBus,
                onClick = { onNavigate(Routes.MapaBus) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Mapa de Buses"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.rojoPrincipal),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.2f)
                )
            )

            NavigationBarItem(
                selected = currentRoute == Routes.Cartera,
                onClick = { onNavigate(Routes.Cartera) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Cartera"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.rojoPrincipal),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.2f)
                )
            )

            NavigationBarItem(
                selected = currentRoute == Routes.Tienda,
                onClick = { onNavigate(Routes.Tienda) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Tienda"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.rojoPrincipal),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.2f)
                )
            )
        }
    }
}