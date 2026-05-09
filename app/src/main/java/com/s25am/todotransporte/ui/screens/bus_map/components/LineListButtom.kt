package com.s25am.todotransporte.ui.screens.bus_map.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.database.data.Linea

/**
 * Botón circular para el filtro de líneas. 
 * Dalton: Se ha usado CircleShape y elevación pa mejorar.
 */
@Composable
fun LineListButtom(
    linea: Linea,
    estaSeleccionada: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (estaSeleccionada) colorResource(id = R.color.RojoP) else Color.White,
        tonalElevation = if (estaSeleccionada) 8.dp else 2.dp,
        shadowElevation = if (estaSeleccionada) 4.dp else 1.dp,
        modifier = Modifier.size(54.dp), // Un poco más grande para que se vea mejr
        border = if (!estaSeleccionada) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            linea.codigo?.let {
                Text(
                    text = it,
                    color = if (estaSeleccionada) Color.White else colorResource(id = R.color.RojoP),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
