package com.s25am.todotransporte.ui.screens.sale_point.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.s25am.todotransporte.database.data.PuntoVenta


@Composable
fun SalePointsDialog(
    puntoSeleccionado: PuntoVenta?,
    onDismiss: () -> Unit
) {
    if (puntoSeleccionado != null) {
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
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = colorResource(id = R.color.rojoPrincipal),
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = translateSalePointType(puntoSeleccionado.tipo),
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
                        color = colorResource(R.color.fondoGrisClaro),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = colorResource(id = R.color.rojoPrincipal),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "UBICACIÓN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.rojoPrincipal)
                            )
                        }
                    }

                    Text(
                        text = puntoSeleccionado.domicilio ?: "Dirección no disponible",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    puntoSeleccionado.serial_emt?.let { serial ->
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.LightGray.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "Terminal EMT: $serial",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
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