package com.s25am.todotransporte.ui.screens.maps.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.database.DataBaseViewModel
import com.s25am.todotransporte.database.Linea
import com.s25am.todotransporte.database.Parada

@Composable
fun StopDialog(
    paradaSeleccionada: Parada?,
    lineaSeleccionada: Linea?,
    proximoBusHora: String?,
    viewModel: DataBaseViewModel
) {
    paradaSeleccionada?.let { parada ->
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogo() },
            title = { Text(text = parada.nombre) },
            text = {
                Column {
                    Text(text = "Línea: ${lineaSeleccionada?.codigo ?: "N/A"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Próximo bus: ${proximoBusHora ?: "Consultando..."}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.cerrarDialogo() }) {
                    Text("Cerrar")
                }
            }
        )
    }
}