package com.s25am.todotransporte.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import com.s25am.todotransporte.R
import com.s25am.todotransporte.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    currentRoute: Any?,
    canNavigateBack: Boolean,
    onBack: () -> Unit
) {
    if (currentRoute != Routes.Login && currentRoute != Routes.Register) {
        val titleText = when (currentRoute) {
            is Routes.Maps -> "Mapa de Líneas"
            is Routes.Schedule -> "Líneas y Horarios"
            is Routes.Wallet -> "Mi Cartera"
            is Routes.SalePoint -> "Puntos de Venta"
            is Routes.Route -> "Planificar Ruta"
            else -> "TodoTransporte"
        }

        TopAppBar(
            title = {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = colorResource(id = R.color.RojoP)
            )
        )
    }
}