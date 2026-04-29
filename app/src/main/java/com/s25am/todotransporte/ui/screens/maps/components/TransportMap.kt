package com.s25am.todotransporte.ui.screens.maps.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.ui.screens.maps.MapsViewModel
import android.graphics.Color as AndroidColor

@OptIn(MapboxExperimental::class)
@Composable
fun TransportMap(
    estadoCamara: MapViewportState,
    lineaSeleccionada: Linea?,
    paradas: List<Parada>,
    viewModel: MapsViewModel
    ) {
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = estadoCamara
    ) {
        lineaSeleccionada?.let { linea ->
            linea.rutaGeojson?.let { stringDelJson ->
                val puntosDeLaRuta = remember(stringDelJson) {
                    try {
                        val feature = Feature.fromJson(stringDelJson)
                        val geometry = feature.geometry() as? LineString
                        geometry?.coordinates() ?: emptyList()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }

                if (puntosDeLaRuta.isNotEmpty()) {
                    val colorLinea = try { AndroidColor.parseColor(linea.color) } catch (e: Exception) { AndroidColor.BLUE }
                    PolylineAnnotation(
                        points = puntosDeLaRuta,
                        lineColorInt = colorLinea, // TODO: poner el color correcto
                        lineWidth = 4.0
                    )
                }
            }
        }

        paradas.forEach { parada ->
            CircleAnnotation(
                point = Point.fromLngLat(parada.longitud, parada.latitud),
                circleRadius = 5.0,
                circleColorInt = AndroidColor.RED, // TODO: poner el color correcto
                circleStrokeWidth = 1.0,
                circleStrokeColorInt = AndroidColor.WHITE,
                onClick = {
                    viewModel.mostrarInfoParada(parada)
                    true
                }
            )
        }
    }
}