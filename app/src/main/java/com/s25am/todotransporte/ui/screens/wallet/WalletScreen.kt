package com.s25am.todotransporte.ui.screens.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.database.data.Ticket
import com.s25am.todotransporte.ui.screens.wallet.components.PurchaseDialog
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

/**
 * Pantalla principal de la Cartera (Wallet).
 * NOTA: El Scaffold y la TopBar se gestionan de forma global en MainActivity.
 */
@Composable
fun WalletScreen(
    viewModel: WalletViewModel = viewModel()
) {
    val availableTickets by viewModel.availableTickets.collectAsState()
    val balance by viewModel.userBalance.collectAsState()
    var ticketToBuy by remember { mutableStateOf<Ticket?>(null) }

    if (ticketToBuy != null) {
        PurchaseDialog(
            ticket = ticketToBuy!!,
            onConfirm = {
                viewModel.buyTicket(ticketToBuy!!)
                ticketToBuy = null
            },
            onDismiss = { ticketToBuy = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SECCIÓN SALDO
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
                
                Button(
                    onClick = { /* TODO: NFC */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Leer Tarjeta (NFC)")
                }
                
                OutlinedButton(
                    onClick = { /* TODO: Recargar */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Recargar Saldo")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // SECCIÓN TIENDA
        Text(
            text = "Comprar Billetes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            availableTickets.forEach { ticket ->
                Button(
                    onClick = { ticketToBuy = ticket },
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

@Preview(showBackground = true)
@Composable
fun WalletPreview() {
    TodoTransporteTheme {
        WalletScreen()
    }
}