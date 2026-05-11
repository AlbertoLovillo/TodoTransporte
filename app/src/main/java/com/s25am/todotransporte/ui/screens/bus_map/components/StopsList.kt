package com.s25am.todotransporte.ui.screens.bus_map.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.ui.theme.GrisFondoCl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsList(
    lineas: List<Linea>,
    paradas: List<Parada>,
    lineaSeleccionada: Linea?,
    direccionActual: Int,
    onAlternarDireccion: () -> Unit,
    onSeleccionarLinea: (Linea) -> Unit,
    isMap: Boolean,
    destino: String? = null
) {
    var rotacionTarget by remember { mutableStateOf(0f) }
    val anguloAnimado by animateFloatAsState(
        targetValue = rotacionTarget,
        animationSpec = tween(durationMillis = 500)
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GrisFondoCl)
    ) {

        Surface(
            color = GrisFondoCl,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val textoSentido =
                    if (isMap) {
                        destino
                    } else {
                        if (direccionActual == 0) "Ida" else "Vuelta"
                    }

                if (lineas.isEmpty()) {
                    Text(
                        text = "Cargando líneas...",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Sentido: $textoSentido",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    rotacionTarget += 360f
                    onAlternarDireccion()
                },
                modifier = Modifier
                    .padding(end = 12.dp)
                    .rotate(anguloAnimado)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.RojoP))
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Cambiar Sentido",
                    tint = Color.White
                )
            }


            HorizontalMultiBrowseCarousel(
                state = rememberCarouselState { lineas.count() },
                modifier = Modifier.weight(1f),
                preferredItemWidth = 50.dp,
                itemSpacing = 10.dp,
                contentPadding = PaddingValues(end = 16.dp)
            ) { index ->
                val linea = lineas[index]

                LineListButtom(
                    linea = linea,
                    estaSeleccionada = (linea.id == lineaSeleccionada?.id),
                    onClick = { onSeleccionarLinea(linea) }
                )
            }
        }

    }

}
