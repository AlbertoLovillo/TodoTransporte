package com.s25am.todotransporte.ui.screens.tickets.shop.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SaldoInsuficienteDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Saldo Insuficiente", fontWeight = FontWeight.Bold)
        },
        text = {
            Text("No tienes saldo suficiente para comprar este billete. Por favor, recarga tu monedero.")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido", color = Color.Red)
            }
        }
    )
}