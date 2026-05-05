package com.s25am.todotransporte.ui.screens.schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.database.data.Parada

/**
 * Componente individual: Tarjeta de la parada.
 */
@Composable
fun ItemParada(
    parada: Parada,
    proximoBusHora: String?,
    tieneBusCerca: Boolean,
    onClick: () -> Unit
) {
    var esFavorito by remember { mutableStateOf(false) }
    val colorPrimario = MaterialTheme.colorScheme.primary

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        // TIEMPO REAL: Si hay bus cerca, resaltamos en dorado
                        if (tieneBusCerca) Color(0xFFFFD700).copy(alpha = 0.2f) 
                        else colorPrimario.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    // TIEMPO REAL: Cambiamos icono si hay un bus en directo
                    imageVector = if (tieneBusCerca) Icons.Default.DirectionsBus else Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (tieneBusCerca) Color(0xFFB8860B) else colorPrimario,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = parada.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (tieneBusCerca) {
                        // TIEMPO REAL: Etiqueta de aviso en directo
                        Text(
                            text = "● EN DIRECTO ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB8860B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Próximo bus: ${proximoBusHora ?: "Consultando..."}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { esFavorito = !esFavorito }, modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) colorPrimario else MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
