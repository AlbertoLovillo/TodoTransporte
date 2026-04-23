package com.s25am.todotransporte.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.s25am.todotransporte.R
import com.s25am.todotransporte.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    currentRoute: Any?,
    canNavigateBack: Boolean,
    onBack: () -> Unit
) {
    // Solo mostramos la barra si NO estamos en la pantalla de Login
    if (currentRoute != Routes.Login) {
        TopAppBar(
            title = {
                Text(
                    text = "TodoTransporte",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                // Si el MainActivity nos dice que hay páginas atrás, ponemos la flecha
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