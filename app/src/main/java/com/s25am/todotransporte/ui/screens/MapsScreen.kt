package com.s25am.todotransporte.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
            .padding(16.dp)
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


        if(filtroSeleccionado != 2) {
            val selectedOptions = remember {
                mutableStateListOf(false, false, false)
            }
            val options = listOf("Metro", "Autobus", "Cercanias")

            MultiChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        checked = selectedOptions[index],
                        onCheckedChange = {
                            selectedOptions[index] = !selectedOptions[index]
                        },
                        icon = { SegmentedButtonDefaults.Icon(selectedOptions[index]) },
                        label = {
                            when (label) {
                                "Metro" ->
//                                Icon(
//                                    imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
//                                    contentDescription = "Directions Walk"
//                                )
                                    Text("Metro")

                                "Autobus" ->
//                                Icon(
//                                    imageVector = Icons.Default.DirectionsBus,
//                                    contentDescription = "Directions Bus"
//                                )
                                    Text("Autobus")

                                "Cercanias" ->
//                                Icon(
//                                    imageVector = Icons.Default.DirectionsCar,
//                                    contentDescription = "Directions Car"
//                                )
                                    Text("Cercanias")
                            }
                        },
                        modifier = Modifier.height(64.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TODO: Aquí irá el componente real de Google Maps (GoogleMap { ... })
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.LightGray), // Fondo temporal simulando el mapa
            contentAlignment = Alignment.Center
        ) {
            Text("Mapa de Google interactivo aquí")
        }
    }
}

@Preview
@Composable
fun MapsPreview() {
    MapsScreen()
}