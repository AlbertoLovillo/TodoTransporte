package com.s25am.todotransporte.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.R
import com.s25am.todotransporte.ui.screens.schedule.ScheduleViewModel
import com.s25am.todotransporte.ui.screens.wallet.componetsBuy.CardCompra
import com.s25am.todotransporte.ui.screens.wallet.componetsBuy.TicketSearchBar
import com.s25am.todotransporte.ui.screens.wallet.componetsWallet.Tikets
import com.s25am.todotransporte.ui.screens.wallet.viewModel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyTicketScreen(viewModel: WalletViewModel = viewModel(), // Compartimos el mismo ViewModel
                    scheduleViewModel: ScheduleViewModel = viewModel(),
                    onBack: () -> Unit) {
    val lineasReales by scheduleViewModel.lineas.collectAsState()
    var searchText by remember { mutableStateOf("") }

    // Transformamos cada "Linea" en un objeto "Tikets" (tu clase de datos)
    val opcionesCompra = lineasReales.map { linea ->
        Tikets(
            id = linea.id.toString(),
            titulo = "Billete Línea ${linea.nombre}", // Ej: Billete Línea L1
            trayecto = "Trayecto: ${linea.nombre}",   // Aquí colocamos la línea automáticamente
            fecha = "30/04/2026",                   // Fecha fija o calculada
            precio = "1.50€"                         // Precio (podrías añadirlo a la tabla Linea)
        )
    }.filter { it.titulo.contains(searchText, ignoreCase = true) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Comprar Billetes",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.RojoP)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Aplicamos el padding del Scaffold aquí
        ) {
            // 1. LLAMAMOS AL BUSCADOR (Se queda fijo arriba)
            TicketSearchBar(
                query = searchText,
                onQueryChange = { searchText = it }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Selecciona tu tarifa",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                }

                items(opcionesCompra) { opcion ->
                    CardCompra(
                        opcion = opcion,
                        onBuyClick = {
                            viewModel.addTicket(opcion)
                            // TODO: Aquí irá la lógica para insertar en Supabase
                            println("Comprando: ${opcion.titulo}")
                            onBack()
                        }
                    )
                }

                item {
                    // Aviso informativo al final
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(
                                colorResource(id = R.color.rojoFlojito).copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = colorResource(id = R.color.RojoP)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Los billetes comprados aparecerán automáticamente en tu cartera.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

