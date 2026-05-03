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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.R
import com.s25am.todotransporte.ui.screens.wallet.componetsBuy.CardCompra
import com.s25am.todotransporte.ui.screens.wallet.componetsWallet.Tikets
import com.s25am.todotransporte.ui.screens.wallet.viewModel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyTicketScreen(viewModel: WalletViewModel = viewModel(), // Compartimos el mismo ViewModel
                    onBack: () -> Unit) {
    // Lista de ejemplo remplazar despues por lineas quitar tickets esta seria la estructura para dentro de las lineas
    val opcionesCompra = listOf(
        Tikets("1", "Billete Sencillo", "1 Viaje - Validez 2h", "30/04/2026", "1.25€"),
        Tikets("2", "MetroBus 10", "10 Viajes - Multimodal", "30/04/2026", "4.25€"),
        Tikets("3", "Abono Turístico", "Viajes ilimitados - 24h", "30/04/2026", "10€"),
        Tikets("4", "Suplemento Aeropuerto", "Líneas Express", "30/04/2026", "5€")
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Comprar Billetes", fontWeight = FontWeight.Bold, color = colorResource(id = R.color.RojoP)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
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
                CardCompra(opcion = opcion,
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
                        .background(colorResource(id = R.color.rojoFlojito).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = colorResource(id = R.color.RojoP))
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

