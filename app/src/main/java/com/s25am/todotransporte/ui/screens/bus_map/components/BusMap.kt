package com.s25am.todotransporte.ui.screens.bus_map.components

import android.location.Location
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
import com.s25am.todotransporte.database.data.BusPosition
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import android.graphics.Color as AndroidColor

@OptIn(MapboxExperimental::class)
@Composable
fun BusMap(
    estadoCamara: MapViewportState,
    lineaSeleccionada: Linea?,
    rutaGeojson: String?,
    paradas: List<Parada>,
    busesEnTiempoReal: List<BusPosition>,
    ubicacionUsuario: Location?,
    onParadaClick: (Parada) -> Unit // Recibimos el evento en lugar del ViewModel
) {
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = estadoCamara
    ) {
        lineaSeleccionada?.let { linea ->
            // Usamos la ruta que nos viene del estado
            rutaGeojson?.let { stringDelJson ->
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
                    val colorLinea = try {
                        AndroidColor.parseColor(linea.color)
                    } catch (e: Exception) {
                        AndroidColor.RED
                    }
                    PolylineAnnotation(
                        points = puntosDeLaRuta,
                        lineColorInt = colorLinea,
                        lineWidth = 4.0
                    )
                }
            }
        }

        paradas.forEach { parada ->
            CircleAnnotation(
                point = Point.fromLngLat(parada.longitud, parada.latitud),
                circleRadius = 5.0,
                circleColorInt = AndroidColor.RED,
                circleStrokeWidth = 1.0,
                circleStrokeColorInt = AndroidColor.WHITE,
                onClick = {
                    onParadaClick(parada) // Disparamos el evento
                    true
                }
            )
        }

        ubicacionUsuario?.let { ubicacion ->
            CircleAnnotation(
                point = Point.fromLngLat(ubicacion.longitude, ubicacion.latitude),
                circleRadius = 8.0,
                circleColorInt = AndroidColor.BLUE,
                circleStrokeWidth = 2.0,
                circleStrokeColorInt = AndroidColor.WHITE
            )
        }

        busesEnTiempoReal.forEach { bus ->
            CircleAnnotation(
                point = Point.fromLngLat(bus.lon, bus.lat),
                circleRadius = 7.0,
                circleColorInt = AndroidColor.parseColor("#FFD700"),
                circleStrokeWidth = 1.0,
                circleStrokeColorInt = AndroidColor.BLACK
            )
        }
    }
}