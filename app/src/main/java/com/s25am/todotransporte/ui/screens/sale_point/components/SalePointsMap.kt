package com.s25am.todotransporte.ui.screens.sale_point.components

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.s25am.todotransporte.database.data.PuntoVenta
import com.s25am.todotransporte.ui.screens.sale_point.SalePointViewModel
import android.graphics.Color as AndroidColor

@OptIn(MapboxExperimental::class)
@Composable
fun SalePointsMap(
    estadoCamara: MapViewportState,
    puntosVenta: List<PuntoVenta>,
    viewModel: SalePointViewModel,
    ubicacionUsuario: Location?
) {

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = estadoCamara
    ) {
        puntosVenta.forEach { punto ->
            CircleAnnotation(
                point = Point.fromLngLat(punto.longitud, punto.latitud),
                circleRadius = 6.0,
                circleColorInt = AndroidColor.RED,
                circleStrokeWidth = 1.5,
                circleStrokeColorInt = AndroidColor.WHITE,
                onClick = {
                    viewModel.seleccionarPunto(punto)
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
    }
}
