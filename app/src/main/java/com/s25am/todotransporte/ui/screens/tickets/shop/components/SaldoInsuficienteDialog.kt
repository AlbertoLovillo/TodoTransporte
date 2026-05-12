package com.s25am.todotransporte.ui.screens.tickets.shop.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun SaldoInsuficienteDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White, // Fondo blanco puro
        shape = RoundedCornerShape(28.dp), // Esquinas muy redondeadas modernas
        icon = {
            // Icono de error en la parte superior para que sea más visual
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = colorResource(id = R.color.RojoP),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "¡Ups! Saldo insuficiente",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
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
                Text(
                    text = "No tienes saldo suficiente para comprar este billete.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Por favor, recarga tu monedero en la sección de cuenta.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    "Entendido",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.RojoP)
                )
            }
        }
    )
}