package com.s25am.todotransporte.ui.screens.tickets.wallet.componets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s25am.todotransporte.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeleteTicketDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "circularProgress")
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                "Confirmar borrado",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.rojoPrincipal),
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Mantén pulsado el icono para eliminar definitivamente este billete.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    val job = scope.launch {
                                        val startTime = System.currentTimeMillis()
                                        val duration = 1800L
                                        while (progress < 1f) {
                                            val elapsed = System.currentTimeMillis() - startTime
                                            progress = (elapsed.toFloat() / duration).coerceAtMost(1f)
                                            delay(16)
                                        }
                                        onConfirm()
                                    }
                                    try {
                                        awaitRelease()
                                    } finally {
                                        job.cancel()
                                        progress = 0f
                                    }
                                }
                            )
                        }
                ) {

                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(100.dp),
                        color = colorResource(id = R.color.rojoPrincipal),
                        strokeWidth = 6.dp,
                        trackColor = colorResource(id = R.color.fondoGrisClaro),
                        strokeCap = StrokeCap.Round
                    )

                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .clip(CircleShape)
                            .background(
                                if (progress > 0f)
                                    colorResource(id = R.color.rojoPrincipal).copy(alpha = 0.15f)
                                else
                                    colorResource(id = R.color.fondoGrisClaro)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = if (progress > 0f) colorResource(id = R.color.rojoPrincipal) else Color.DarkGray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
    )
}