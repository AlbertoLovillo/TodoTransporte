package com.s25am.todotransporte.ui.screens.bus_map.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun LineListButtom(
    linea: Linea,
    estaSeleccionada: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (estaSeleccionada) colorResource(id = R.color.rojoPrincipal) else colorResource(id = R.color.rojoMuyFlojito),
        modifier = Modifier.size(50.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            linea.codigo?.let {
                Text(
                    text = it,
                    color = if (estaSeleccionada) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        }
    }
}