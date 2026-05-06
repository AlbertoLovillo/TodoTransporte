package com.s25am.todotransporte.ui.screens.tickets.wallet.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.database.data.Ticket

/**
 * Componente UI: Diálogo de confirmación de compra.
 * Se muestra cuando el usuario hace clic en un billete para comprarlo.
 */
@Composable
fun PurchaseDialog(
    ticket: Ticket,         // El billete que se ha seleccionado para comprar
    onConfirm: () -> Unit,  // Función que se ejecuta al confirmar el pago
    onDismiss: () -> Unit   // Función que se ejecuta al cancelar o cerrar el diálogo
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            // Título principal del diálogo con estilo resaltado
            Text(
                "Confirmar Compra",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono decorativo de billete/confirmación
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Información del billete: Título y descripción breve
                Text(ticket.type, style = MaterialTheme.typography.headlineSmall)
                Text(ticket.description, style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Precio final resaltado en el color primario (RojoP) de la app
                Text(
                    "Precio: ${"%.2f".format(ticket.price)}€",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        confirmButton = {
            // Botón de acción principal: Realiza la compra llamando a onConfirm()
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Pagar Ahora")
            }
        },
        dismissButton = {
            // Botón de cancelar: Cierra el diálogo sin hacer nada
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        // Estética: Bordes muy redondeados para seguir el estilo moderno de la app
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}