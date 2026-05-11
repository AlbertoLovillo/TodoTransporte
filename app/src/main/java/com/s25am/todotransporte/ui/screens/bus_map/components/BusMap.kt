package com.s25am.todotransporte.ui.screens.bus_map.components

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.s25am.todotransporte.R
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
    onParadaClick: (Parada) -> Unit
) {
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = estadoCamara
    ) {
        lineaSeleccionada?.let { linea ->
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
                    val colorLinea = colorResource(id = R.color.rojoMuyFlojito).toArgb()
                    PolylineAnnotation(
                        points = puntosDeLaRuta,
                        lineColorInt = colorLinea,
                        lineWidth = 4.0
                    )
                }
            }
        }

        paradas.forEach { parada ->
            val colorLinea = colorResource(id = R.color.RojoP).toArgb()
            CircleAnnotation(
                point = Point.fromLngLat(parada.longitud, parada.latitud),
                circleRadius = 5.0,
                circleColorInt = colorLinea,
                circleStrokeWidth = 1.0,
                circleStrokeColorInt = AndroidColor.WHITE,
                onClick = {
                    onParadaClick(parada)
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