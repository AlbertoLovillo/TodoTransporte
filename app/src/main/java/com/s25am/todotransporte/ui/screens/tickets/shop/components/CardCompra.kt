package com.s25am.todotransporte.ui.screens.tickets.shop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.database.data.Billete
import com.s25am.todotransporte.ui.theme.Negro

@Composable
fun CardCompra(
    opcion: Billete,
    onBuyClick: () -> Unit,
    onVerMapa: (String) -> Unit // Para que el botón de atrás funcione
) {
    var isRotated by remember { mutableStateOf(false) }

    FlipCard(
        isRotated = isRotated,
        onToggleRotation = { isRotated = !isRotated },
        modifier = Modifier.fillMaxWidth(),
        frontSide = {
            // --- DISEÑO OPTIMIZADO (Cara A) ---
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colorResource(id = R.color.RojoP).copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = colorResource(id = R.color.RojoP),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            opcion.titulo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.rojoFlojito)
                        )

                        Text(
                            opcion.trayecto,
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            color = Color.Gray,
                            maxLines = 3,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )

                        Text(
                            opcion.fecha,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )

                        Text(
                            opcion.precio,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorResource(id = R.color.RojoP),
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onBuyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.RojoP)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp),
                            tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Añadir", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        },
        backSide = {
            // --- EL DISEÑO NUEVO (Cara B) ---
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = Negro,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("¿Quieres ver el recorrido?", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onVerMapa(opcion.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = colorResource(id = R.color.RojoP))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver en BusMap", color = colorResource(id = R.color.RojoP))
                    }

                    Text("Toca para comprar", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    )
}