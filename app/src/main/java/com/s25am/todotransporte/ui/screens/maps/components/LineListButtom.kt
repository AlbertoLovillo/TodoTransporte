package com.s25am.todotransporte.ui.screens.maps.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.s25am.todotransporte.database.Linea

@Composable
fun LineListButtom(linea: Linea, estaSeleccionada: Boolean, onClick: () -> Unit) {
    val colorBase = try {
        Color(linea.color.toColorInt())
    } catch (e: Exception) {
        Color.Blue
    }

    val colorFondo = if (estaSeleccionada) colorBase else Color.White
    val colorTexto = if (estaSeleccionada) Color.White else Color.Black

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorFondo)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = linea.codigo ?: linea.id.toString(),
            color = colorTexto,
            fontWeight = FontWeight.Bold
        )
    }
}