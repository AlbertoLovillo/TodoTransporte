package com.s25am.todotransporte.ui.screens.bus_map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada

@Composable
fun StopDialog(
    paradaSeleccionada: Parada?,
    lineaSeleccionada: Linea?,
    proximoBusHora: String?,
    onDismiss: () -> Unit
) {
    paradaSeleccionada?.let { parada ->
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            shape = RoundedCornerShape(28.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            colorResource(id = R.color.rojoPrincipal).copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = colorResource(id = R.color.rojoPrincipal),
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = parada.nombre,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = colorResource(id = R.color.rojoFlojito).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Línea ${lineaSeleccionada?.codigo ?: "N/A"}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.rojoFlojito)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                colorResource(id = R.color.fondoGrisClaro),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = colorResource(id = R.color.rojoPrincipal),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PRÓXIMO BUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            Text(
                                text = proximoBusHora ?: "Consultando...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.rojoPrincipal)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }
}