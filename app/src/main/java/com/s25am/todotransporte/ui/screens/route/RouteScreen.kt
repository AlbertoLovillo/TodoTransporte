package com.s25am.todotransporte.ui.screens.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.ui.screens.route.components.OfflineTicketCard
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

/**
 * Pantalla para la planificación de rutas.
 * NOTA: El Scaffold y la TopBar se gestionan de forma global en MainActivity.
 */
@Composable
fun RouteScreen(
    viewModel: RouteViewModel = viewModel()
) {
    val origen by viewModel.origen.collectAsState()
    val destino by viewModel.destino.collectAsState()
    val recentTickets by viewModel.recentTickets.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Formulario de búsqueda
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = origen,
                    onValueChange = { viewModel.updateOrigen(it) },
                    label = { Text("Origen") },
                    placeholder = { Text("¿Desde dónde sales?") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = destino,
                    onValueChange = { viewModel.updateDestino(it) },
                    label = { Text("Destino") },
                    placeholder = { Text("¿A dónde vas?") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.buscarRuta() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Route, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar Ruta", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Billetes Offline Recientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Listado de billetes guardados para acceso rápido sin internet
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(recentTickets) { ticket ->
                OfflineTicketCard(ticketName = ticket)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoutePreview() {
    TodoTransporteTheme {
        RouteScreen()
    }
}