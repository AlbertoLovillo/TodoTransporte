package com.s25am.todotransporte.ui.screens.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.QrCode
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.R
import com.s25am.todotransporte.ui.screens.wallet.componets.TicketItem
import com.s25am.todotransporte.ui.screens.wallet.componets.generarQR
import com.s25am.todotransporte.ui.screens.wallet.viewModel.WalletViewModel
import com.s25am.todotransporte.ui.screens.wallet.componets.QrDialog

@Composable
fun WalletScreen(viewModel: WalletViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    //Guardar el id del billete para generar QR
    var billeteSeleccionadoId by remember { mutableStateOf<String?>(null) }
    //Si el ID no es nulo, mostramos el diálogo
    billeteSeleccionadoId?.let { id ->
        QrDialog(
            ticketId = id,
            onDismiss = { billeteSeleccionadoId = null }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // --- CABECERA ---
        item {
            Text(
                "Mi Cartera de Transporte",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.RojoP)
            )
        }

        // --- SECCIÓN TARJETA FÍSICA
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tarjeta Física (NFC)", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { /* TODO: NFC */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.RojoP))
                    ) {
                        Text("Leer Tarjeta")
                    }
                    Button(
                        onClick = { /* TODO: NFC */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.RojoP))
                    ) {
                        Text("Recargar saldo")
                    }
                }
            }
        }

        // --- SECCIÓN MIS BILLETES (La lista dinámica) ---
        item {
            Text(
                "Mis Billetes Activos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Aquí dibujamos cada billete de la lista
        items(uiState.listaTikets) { billete ->
            TicketItem(
                ticket = billete,
                onQrClick = {
                    billeteSeleccionadoId = billete.id
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalletPreview() {
    WalletScreen()
}