package com.s25am.todotransporte.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

/**
 * 1. MODELO DE DATOS
 */
data class DatosLinea(
    val numero: String,
    val destino: String,
    val tiempoLlegada: String
)

/**
 * 2. COMPONENTE INDIVIDUAL (Tarjeta de la línea)
 */
@Composable
fun ItemLinea(datos: DatosLinea) {
    var esFavorito by remember { mutableStateOf(false) }
    val colorPrimario = MaterialTheme.colorScheme.primary

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
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
            // Icono con fondo circular
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(colorPrimario.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = colorPrimario,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Información central
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Línea ${datos.numero}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (datos.tiempoLlegada.contains("min")) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = colorPrimario,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "EN RUTA",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = "Hacia: ${datos.destino}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Tiempo y corazon de favorito
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = datos.tiempoLlegada,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (datos.tiempoLlegada.contains("min")) colorPrimario else MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.ExtraBold
                )
                
                IconButton(
                    onClick = { esFavorito = !esFavorito },
                    modifier = Modifier.size(32.dp)
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
}
/**
 * 3. PANTALLA PRINCIPAL
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Líneas y Horarios",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { rellenos ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(rellenos)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // =========================================================================
                // DATOS DE PRUEBA (para reemplazar por los reales)
                // Solo para la previsualizacion
                // =========================================================================
                val listaLineas = listOf(
                    DatosLinea("101", "Terminal Central", "5 min"),
                    DatosLinea("205", "Plaza Norte", "12 min"),
                    DatosLinea("312", "Estación Sur", "20 min"),
                    DatosLinea("404", "Barrio Oeste", "2 min"),
                    DatosLinea("500", "Hospital General", "15 min"),
                    DatosLinea("110", "Puerto Local", "8 min"),
                    DatosLinea("222", "Zona Universitaria", "No disponible")
                )

                items(listaLineas) { linea ->
                    ItemLinea(linea)
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SchedulePreview() {
    TodoTransporteTheme {
        ScheduleScreen()
    }
}
