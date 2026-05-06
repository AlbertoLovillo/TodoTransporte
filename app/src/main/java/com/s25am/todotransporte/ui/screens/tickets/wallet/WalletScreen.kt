package com.s25am.todotransporte.ui.screens.tickets.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.R
import com.s25am.todotransporte.ui.screens.tickets.viewModel.TicketsViewModel
import com.s25am.todotransporte.ui.screens.tickets.wallet.componetsWallet.QrDialog
import com.s25am.todotransporte.ui.screens.tickets.wallet.componetsWallet.SwipeableTicketItem
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

/**
 * Pantalla principal de la Cartera (Wallet).
 * NOTA: El Scaffold y la TopBar se gestionan de forma global en MainActivity.
 */
@Composable
fun WalletScreen(
    viewModel: TicketsViewModel = viewModel()
) {

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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // Aquí fuerzas el fondo blanco
    ) {

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
            //Saldo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.RojoP)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Saldo Disponible",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp) // Espaciado arriba y abajo de la línea
                                .fillMaxWidth(),           // Recorre la caja de izquierda a derecha
                            thickness = 4.dp,              // Grosor de la línea
                            color = Color.White
                        )

                        // USAMOS EL SALDO REAL DEL UI STATE
                        // String.format("%.2f", ...) sirve para mostrar siempre 2 decimales (0,00)
                        Text(
                            text = "${String.format("%.2f", uiState.saldo)} €",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // BOTÓN RECARGAR CONECTADO
                            Button(
                                onClick = {
                                    // Aquí llamamos a la función del ViewModel que creamos antes
                                    viewModel.recargarSaldo(10.0)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = colorResource(id = R.color.RojoP)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Recargar 10€", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { /* TODO: Historial */ },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = colorResource(id = R.color.RojoP)
                                ),
                                border = BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Historial")
                            }
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
            items(uiState.listaTickets, key = { it.id }) { billete -> // Usar key mejora las animaciones
                SwipeableTicketItem(
                    ticket = billete,
                    onQrClick = { billeteSeleccionadoId = billete.id },
                    onDeleteConfirm = {
                        viewModel.deleteTicket(billete) // Llamamos a la función de borrado
                    }
                )
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