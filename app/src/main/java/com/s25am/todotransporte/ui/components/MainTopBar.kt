package com.s25am.todotransporte.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    currentRoute: Any?,
    canNavigateBack: Boolean,
    saldo: Double,
    onBack: () -> Unit
) {

    if (currentRoute != Routes.Login && currentRoute != Routes.Register) {
        val titleText = when (currentRoute) {
            is Routes.SalePoint -> "Puntos de Venta"
            is Routes.Schedule -> "Horarios"
            is Routes.Maps -> "Mapa de Líneas"
            is Routes.Wallet -> "Mi Cartera"
            is Routes.Shop -> "Compra de Billetes"
            else -> "TodoTransporte"
        }

        TopAppBar(
            title = {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            },
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás",
                            tint = colorResource(id = R.color.RojoP)
                        )
                    }
                }
            },
            actions = {
                if (currentRoute == Routes.Shop) {
                    Text(
                        text = "${String.format("%.2f", saldo)} €",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = colorResource(id = R.color.RojoP)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = colorResource(id = R.color.RojoP)
            )
        )
    }
}
