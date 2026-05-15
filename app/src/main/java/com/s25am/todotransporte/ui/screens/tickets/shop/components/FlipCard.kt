package com.s25am.todotransporte.ui.screens.tickets.shop.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun FlipCard(
    isRotated: Boolean,
    onToggleRotation: () -> Unit,
    frontSide: @Composable () -> Unit,
    backSide: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "CardRotacion"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 15f * density
            }
            .clickable { onToggleRotation() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.2f))

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (rotation <= 90f) {
                Box(Modifier.fillMaxSize()){
                frontSide()
                }
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                ) {
                    backSide()
                }
            }
        }
    }
}