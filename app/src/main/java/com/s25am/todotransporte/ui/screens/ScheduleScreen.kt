package com.s25am.todotransporte.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ScheduleScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Líneas y Horarios", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            // Simulamos 3 líneas para la prueba visual
            items(3) { index ->
                LineItem(numeroLinea = index + 1)
            }
        }
    }
}

@Composable
fun LineItem(numeroLinea: Int) {
    var guardadoOffline by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Línea $numeroLinea", style = MaterialTheme.typography.titleLarge)
                Text("Próximo bus: 5 min", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { guardadoOffline = !guardadoOffline }) {
                Icon(
                    imageVector = if (guardadoOffline) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Guardar offline",
                    tint = if (guardadoOffline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
fun SchedulePreview() {
    ScheduleScreen()
}