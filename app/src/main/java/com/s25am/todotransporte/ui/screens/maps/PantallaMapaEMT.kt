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
import com.s25am.todotransporte.database.DataBaseViewModel
import com.s25am.todotransporte.ui.screens.maps.components.StopDialog
import com.s25am.todotransporte.ui.screens.maps.components.StopsList
import com.s25am.todotransporte.ui.screens.maps.components.TransportMap

@OptIn(MapboxExperimental::class)
@Composable
fun MapsScreen(viewModel: DataBaseViewModel = viewModel()) {
    val lineas by viewModel.lineas.collectAsState()
    val lineaSeleccionada by viewModel.selectedLinea.collectAsState()
    val paradas by viewModel.paradas.collectAsState()
    val paradaSeleccionada by viewModel.paradaSeleccionada.collectAsState()
    val proximoBusHora by viewModel.proximoBusHora.collectAsState()

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
            viewModel = viewModel
        )


        StopDialog(
            paradaSeleccionada = paradaSeleccionada,
            lineaSeleccionada = lineaSeleccionada,
            proximoBusHora = proximoBusHora,
            viewModel = viewModel
        )

//        MapboxMap(
//            modifier = Modifier.fillMaxSize(),
//            mapViewportState = estadoCamara
//        ) {
//            lineaSeleccionada?.let { linea ->
//                linea.rutaGeojson?.let { stringDelJson ->
//                    val puntosDeLaRuta = remember(stringDelJson) {
//                        try {
//                            val feature = Feature.fromJson(stringDelJson)
//                            val geometry = feature.geometry() as? LineString
//                            geometry?.coordinates() ?: emptyList()
//                        } catch (e: Exception) {
//                            emptyList()
//                        }
//                    }
//
//                    if (puntosDeLaRuta.isNotEmpty()) {
//                        val colorLinea = try { AndroidColor.parseColor(linea.color) } catch (e: Exception) { AndroidColor.BLUE }
//                        PolylineAnnotation(
//                            points = puntosDeLaRuta,
//                            lineColorInt = colorLinea, // TODO: poner el color correcto
//                            lineWidth = 4.0
//                        )
//                    }
//                }
//            }
//
//            paradas.forEach { parada ->
//                CircleAnnotation(
//                    point = Point.fromLngLat(parada.longitud, parada.latitud),
//                    circleRadius = 5.0,
//                    circleColorInt = AndroidColor.RED, // TODO: poner el color correcto
//                    circleStrokeWidth = 1.0,
//                    circleStrokeColorInt = AndroidColor.WHITE,
//                    onClick = {
//                        viewModel.mostrarInfoParada(parada)
//                        true
//                    }
//                )
//            }
//        }


//        Column(
//            modifier = Modifier.align(Alignment.BottomCenter)
//        ) {
//            if (lineas.isEmpty()) {
//                Text(
//                    text = "Cargando líneas...",
//                    color = Color.Red
//                )
//            } else {
//                Text(
//                    text = "Paradas cargadas: ${paradas.size}",
//                    modifier = Modifier.padding(8.dp)
//                )
//                LazyRow(
//                    modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
//                    contentPadding = PaddingValues(horizontal = 16.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(lineas) { linea ->
//                        BotonLinea(
//                            linea = linea,
//                            estaSeleccionada = (linea.id == lineaSeleccionada?.id),
//                            onClick = { viewModel.seleccionarLinea(linea) }
//                        )
//                    }
//                }
//            }
//        }


//        paradaSeleccionada?.let { parada ->
//            AlertDialog(
//                onDismissRequest = { viewModel.cerrarDialogo() },
//                title = { Text(text = parada.nombre) },
//                text = {
//                    Column {
//                        Text(text = "Línea: ${lineaSeleccionada?.codigo ?: "N/A"}")
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = "Próximo bus: ${proximoBusHora ?: "Consultando..."}",
//                            fontWeight = FontWeight.Bold,
//                            color = Color(0xFF1B5E20)
//                        )
//                    }
//                },
//                confirmButton = {
//                    Button(onClick = { viewModel.cerrarDialogo() }) {
//                        Text("Cerrar")
//                    }
//                }
//            )
//        }
    }
}

//@Composable
//fun LineListButtom(linea: Linea, estaSeleccionada: Boolean, onClick: () -> Unit) {
//    val colorBase = try {
//        Color(linea.color.toColorInt())
//    } catch (e: Exception) {
//        Color.Blue
//    }
//
//    val colorFondo = if (estaSeleccionada) colorBase else Color.White
//    val colorTexto = if (estaSeleccionada) Color.White else Color.Black
//
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = Modifier
//            .size(60.dp)
//            .clip(RoundedCornerShape(12.dp))
//            .background(colorFondo)
//            .clickable { onClick() }
//            .padding(8.dp)
//    ) {
//        Text(
//            text = linea.codigo ?: linea.id.toString(),
//            color = colorTexto,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}