package com.s25am.todotransporte.ui.screens.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

/**
 * Pantalla principal de la Cartera (Wallet).
 * Aquí el usuario puede ver su saldo disponible y comprar billetes o bonos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: WalletViewModel = viewModel() // Inyección del ViewModel para gestionar datos
) {
    // Observamos el estado del saldo y los billetes disponibles desde el ViewModel
    val availableTickets by viewModel.availableTickets.collectAsState()
    val balance by viewModel.userBalance.collectAsState()
    
    // Estado interno para saber qué billete se ha pulsado y mostrar el diálogo de compra
    var ticketToBuy by remember { mutableStateOf<Ticket?>(null) }

    // Si el usuario ha seleccionado un billete, mostramos el componente PurchaseDialog
    if (ticketToBuy != null) {
        PurchaseDialog(
            ticket = ticketToBuy!!,
            onConfirm = {
                // Confirmamos la compra en el ViewModel
                viewModel.buyTicket(ticketToBuy!!)
                ticketToBuy = null // Cerramos el diálogo tras la compra
            },
            onDismiss = { ticketToBuy = null } // Simplemente cerramos el diálogo al cancelar
        )
    }

    Scaffold(
        topBar = {
            // Cabecera superior idéntica a ScheduleScreen para mantener coherencia visual
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mi Cartera",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SECCIÓN SALDO: Muestra el balance y opciones de gestión (NFC/Recarga)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Saldo Disponible", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${"%.2f".format(balance)}€",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Acción para leer una tarjeta de transporte física mediante el chip NFC
                    Button(
                        onClick = { /* TODO: Lógica de hardware para NFC */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Leer Tarjeta (NFC)")
                    }
                    
                    // Acción para añadir dinero al saldo virtual de la aplicación
                    OutlinedButton(
                        onClick = { /* TODO: Lógica de pasarela de pagos */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Recargar Saldo")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // SECCIÓN TIENDA: Listado de billetes disponibles (estilo RouteScreen)
            Text(
                text = "Comprar Billetes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Generamos dinamicamente los botones de compra basados en los datos del ViewModel
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                availableTickets.forEach { ticket ->
                    Button(
                        onClick = { ticketToBuy = ticket }, // Abrimos confirmación al pulsar
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ticket.type, 
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${"%.2f".format(ticket.price)}€",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalletPreview() {
    TodoTransporteTheme {
        WalletScreen()
    }
}
