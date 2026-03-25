package com.s25am.todotransporte.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MapsScreen() {
    var filtroSeleccionado by remember { mutableStateOf("Paradas") }
    val filtros = listOf("Paradas", "Tiempo Real", "Puntos de Venta")

    Box(modifier = Modifier.fillMaxSize()) {
        // TODO: Aquí irá el componente real de Google Maps (GoogleMap { ... })
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray), // Fondo temporal simulando el mapa
            contentAlignment = Alignment.Center
        ) {
            Text("Mapa de Google interactivo aquí")
        }

        // Fila de botones flotantes sobre el mapa para los filtros
        LazyRow(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            items(filtros.size) { index ->
                val filtro = filtros[index]
                ElevatedFilterChip(
                    selected = filtroSeleccionado == filtro,
                    onClick = { filtroSeleccionado = filtro },
                    label = { Text(filtro) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun MapsPreview() {
    MapsScreen()
}