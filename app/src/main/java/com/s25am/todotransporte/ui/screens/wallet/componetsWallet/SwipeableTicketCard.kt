package com.s25am.todotransporte.ui.screens.wallet.componetsWallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTicketItem(
    ticket: Tikets,
    onQrClick: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Estado del gesto de deslizar
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDialog = true // Al deslizar a la izquierda, abrimos el aviso
                false // No borramos la fila todavía, esperamos al Dialog
            } else {
                false
            }
        }
    )

    // Si cerramos el diálogo sin borrar, devolvemos la tarjeta a su sitio
    LaunchedEffect(showDialog) {
        if (!showDialog) dismissState.reset()
    }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Eliminar billete?") },
            text = { Text("Este billete desaparecerá de tu cartera. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteConfirm()
                    showDialog = false
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false, // Solo deslizar hacia la izquierda
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart){
                colorResource(id = R.color.rojoFlojito)
            }else{
                Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
            }
        }
    ) {
        TicketItem(ticket = ticket, onQrClick = onQrClick)
    }
}