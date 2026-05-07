package com.s25am.todotransporte.ui.screens.sale_point.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.database.data.PuntoVenta


@Composable
fun SalePointsDialog(
    puntoSeleccionado: PuntoVenta?,
    onDismiss: () -> Unit
) {
    if (puntoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = translateSalePointType(puntoSeleccionado.tipo),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Dirección:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = puntoSeleccionado.domicilio ?: "Dirección no disponible",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    puntoSeleccionado.serial_emt?.let { serial ->
                        Text(
                            text = "Código EMT: $serial",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}