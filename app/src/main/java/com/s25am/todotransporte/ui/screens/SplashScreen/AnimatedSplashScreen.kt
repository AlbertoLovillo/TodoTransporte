package com.s25am.todotransporte.ui.screens.SplashScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import com.s25am.todotransporte.R
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(onNavigationNext: () -> Unit) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components { add(GifDecoder.Factory()) }
        .build()

    val customRed = Color(0xFFF73E4B)

    var progress by remember { mutableStateOf(0f) }
    var backgroundAlpha by remember { mutableStateOf(1f) }
    var isContentVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(40)
            progress += 0.03f
        }

        isContentVisible = false
        delay(100)

        repeat(10) {
            delay(30)
            backgroundAlpha -= 0.1f
        }

        onNavigationNext()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(customRed.copy(alpha = backgroundAlpha)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isContentVisible) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = R.drawable.gif_bus,
                    contentDescription = null,
                    imageLoader = imageLoader,
                    modifier = Modifier.size(300.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(8.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}