package com.s25am.todotransporte.ui.screens.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MapsScreen() {
    var filtroSeleccionado by remember { mutableIntStateOf(0) }
    val filtros = listOf("Transporte", "Puntos de Venta")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // Fila de botones flotantes sobre el mapa para los filtros

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            filtros.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = filtros.size
                    ),
                    onClick = { filtroSeleccionado = index },
                    selected = index == filtroSeleccionado,
                    label = { Text(label) },
                    modifier = Modifier.height(64.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Green),
            contentAlignment = Alignment.Center
        ) {
//            MapComponent()
        }
    }
}

@Preview
@Composable
fun MapsPreview() {
    MapsScreen()
}