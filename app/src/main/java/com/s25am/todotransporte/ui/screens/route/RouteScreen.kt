package com.s25am.todotransporte.ui.screens.route

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Planifica tu Ruta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Formulario de búsqueda
        OutlinedTextField(
            value = origen,
            onValueChange = { origen = it },
            label = { Text("Origen") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = destino,
            onValueChange = { destino = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Lógica de calcular ruta y coste */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar Ruta")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Billetes Offline Recientes", style = MaterialTheme.typography.titleMedium)
        // Aquí iría una LazyRow o un Card con el billete guardado para acceso rápido sin internet
    }
}

@Preview
@Composable
fun HomePreview() {
    HomeScreen()
}