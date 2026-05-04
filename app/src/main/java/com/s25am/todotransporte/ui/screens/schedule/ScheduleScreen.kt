package com.s25am.todotransporte.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.database.data.Horario
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.ui.screens.bus_map.components.LineListButtom
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

/**
 * 1. COMPONENTE INDIVIDUAL (Tarjeta de la parada)
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

/**
 * 2. DIÁLOGO DE HORARIOS
 */
@Composable
fun AlertDialogParada(
    parada: Parada,
    horarios: List<Horario>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Horarios: ${parada.nombre}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (horarios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay horarios disponibles o cargando...")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(horarios) { horario ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Próxima llegada")
                            }
                            Text(
                                text = horario.hora_llegada,
                                style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

/**
 * 3. PANTALLA PRINCIPAL
 */
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    val lineas by viewModel.lineas.collectAsState()
    val lineaSeleccionada by viewModel.selectedLinea.collectAsState()
    val paradas by viewModel.paradas.collectAsState()
    val proximosBuses by viewModel.proximosBusesParadas.collectAsState()
    val paradaSeleccionada by viewModel.paradaSeleccionada.collectAsState()
    val horariosParada by viewModel.horariosParada.collectAsState()
    val direccionActual by viewModel.direccionActual.collectAsState()
    val paradasConBus by viewModel.paradasConBusEnTiempoReal.collectAsState()

    ScheduleContent(
        lineas = lineas,
        lineaSeleccionada = lineaSeleccionada,
        paradas = paradas,
        proximosBuses = proximosBuses,
        paradaSeleccionada = paradaSeleccionada,
        horariosParada = horariosParada,
        direccionActual = direccionActual,
        paradasConBus = paradasConBus,
        onAlternarDireccion = { viewModel.alternarDireccion() },
        onSeleccionarLinea = { viewModel.seleccionarLinea(it) },
        onMostrarInfoParada = { viewModel.mostrarInfoParada(it) },
        onCerrarDialogo = { viewModel.cerrarDialogo() }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleContent(
    lineas: List<Linea>,
    lineaSeleccionada: Linea?,
    paradas: List<Parada>,
    proximosBuses: Map<Int, String>,
    paradaSeleccionada: Parada?,
    horariosParada: List<Horario>,
    direccionActual: Int,
    paradasConBus: Set<Int>,
    onAlternarDireccion: () -> Unit,
    onSeleccionarLinea: (Linea) -> Unit,
    onMostrarInfoParada: (Parada) -> Unit,
    onCerrarDialogo: () -> Unit
) {
    if (paradaSeleccionada != null) {
        AlertDialogParada(
            parada = paradaSeleccionada,
            horarios = horariosParada,
            onDismiss = onCerrarDialogo
        )
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Líneas y Horarios",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }) { rellenos ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(rellenos)
                .background(MaterialTheme.colorScheme.background)
        ) {

            val textoSentido = if (direccionActual == 0) "Ida" else "Vuelta"
            Text(
                text = "Sentido: $textoSentido",
                modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onAlternarDireccion,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Cambiar Sentido",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                LazyRow(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lineas) { linea ->
                        LineListButtom(
                            linea = linea,
                            estaSeleccionada = linea.id == lineaSeleccionada?.id,
                            onClick = { onSeleccionarLinea(linea) }
                        )
                    }
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(paradas) { parada ->
                    ItemParada(
                        parada = parada,
                        proximoBusHora = proximosBuses[parada.id],
                        tieneBusCerca = paradasConBus.contains(parada.id),
                        onClick = { onMostrarInfoParada(parada) }
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SchedulePreview() {
    TodoTransporteTheme {
        val dummyLinea = Linea(id = 1, codigo = "L1", nombre = "Línea 1", color = "#FF0000", rutaGeojson = null)
        val dummyParada = Parada(id = 1, nombre = "Parada Central", latitud = 0.0, longitud = 0.0)

        ScheduleContent(
            lineas = listOf(dummyLinea),
            lineaSeleccionada = dummyLinea,
            paradas = listOf(dummyParada),
            proximosBuses = mapOf(1 to "10:30"),
            paradaSeleccionada = null,
            horariosParada = emptyList(),
            direccionActual = 0,
            paradasConBus = emptySet(),
            onAlternarDireccion = {},
            onSeleccionarLinea = {},
            onMostrarInfoParada = {},
            onCerrarDialogo = {}
        )
    }
}