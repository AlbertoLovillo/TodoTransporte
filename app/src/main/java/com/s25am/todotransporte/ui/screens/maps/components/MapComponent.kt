package com.s25am.todotransporte.ui.screens.maps.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation

@OptIn(MapboxExperimental::class)
@Composable
fun MapComponent(
    // TODO: viewModel heredado entre componentes de una misma pantalla
    // TODO: parametro de ruta concreta y que salga su linea y paradas
) {
    // 1. Controlamos dónde mira la cámara al inicio --- Seria interesante que fuera la estacion de buses o la ubicacion de la persona
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(-3.703790, 40.416775)) // Longitud, Latitud
            zoom(12.0)
        }
    }

    // 2. Definimos los puntos de nuestra ruta de bus --- Esto iria en la base de datos y se controlaria por el viewModel
    val rutaBus = listOf(
        Point.fromLngLat(-3.71, 40.41),
        Point.fromLngLat(-3.70, 40.42),
        Point.fromLngLat(-3.69, 40.43)
    )

    // 3. Pintamos el mapa --- Se puede cambiar el estilo e incluso personalizarlo
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        mapInitOptionsFactory = { context ->
            MapInitOptions(
                context = context,
                styleUri = com.mapbox.maps.Style.LIGHT
            )
        }
    ) {
        // --- AQUÍ DIBUJAMOS SOBRE EL MAPA ---

        // Dibujar la línea de la ruta
        PolylineAnnotation(
            points = rutaBus,
            lineColorString = "#FF0000", // Ruta en rojo
            lineWidth = 5.0
        )

        // Dibujar una parada de autobús (marcador)
        PointAnnotation(
            point = Point.fromLngLat(-3.70, 40.42)
            // Aquí puedes añadir un icono personalizado más adelante con 'iconImage'
        )
    }
}