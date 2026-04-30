package com.s25am.todotransporte.ui.screens.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.s25am.todotransporte.ui.screens.maps.components.StopDialog
import com.s25am.todotransporte.ui.screens.maps.components.StopsList
import com.s25am.todotransporte.ui.screens.maps.components.TransportMap

@OptIn(MapboxExperimental::class)
@Composable
fun MapsScreen(
    viewModel: MapsViewModel = viewModel()
) {
    val lineas by viewModel.lineas.collectAsState()
    val lineaSeleccionada by viewModel.selectedLinea.collectAsState()
    val paradas by viewModel.paradas.collectAsState()
    val paradaSeleccionada by viewModel.paradaSeleccionada.collectAsState()
    val proximoBusHora by viewModel.proximoBusHora.collectAsState()
    val direccionActual by viewModel.direccionActual.collectAsState()

    val estadoCamara = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(-4.4214, 36.7213))
            zoom(12.0)
            pitch(0.0)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {
            TransportMap(
                estadoCamara = estadoCamara,
                lineaSeleccionada = lineaSeleccionada,
                paradas = paradas,
                viewModel = viewModel
            )
        }

        StopsList(
            lineas = lineas,
            paradas = paradas,
            lineaSeleccionada = lineaSeleccionada,
            viewModel = viewModel,
            direccionActual = direccionActual
        )


        StopDialog(
            paradaSeleccionada = paradaSeleccionada,
            lineaSeleccionada = lineaSeleccionada,
            proximoBusHora = proximoBusHora,
            viewModel = viewModel
        )
    }
}