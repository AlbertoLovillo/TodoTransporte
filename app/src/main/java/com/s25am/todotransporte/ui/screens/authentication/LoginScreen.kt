package com.s25am.todotransporte.ui.screens.authentication


import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.s25am.todotransporte.R
import com.s25am.todotransporte.ui.screens.authentication.viewmodel.AuthenticationViewModel


@OptIn(UnstableApi::class)
@Composable
fun LoginScreen(
    viewModel: AuthenticationViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current


    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.video_fondo_auth}")
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            prepare()
            playWhenReady = true
        }
    }


    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )


        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 20.dp)
                    .verticalScroll(rememberScrollState()),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.logo_sin_fondo),
                        contentDescription = "Logo TodoTransporte",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .padding(bottom = 8.dp),
                        contentScale = ContentScale.Fit
                    )


                    Text(
                        "TodoTransporte",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.RojoP)
                    )


                    Text(
                        "Inicia sesión para continuar",
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.rojoFlojito)
                    )


                    Spacer(modifier = Modifier.height(32.dp))


                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = colorResource(id = R.color.RojoP)
                        )
                    )


                    Spacer(modifier = Modifier.height(16.dp))


                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = { Text("Contraseña") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = colorResource(id = R.color.RojoP))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = colorResource(id = R.color.RojoP)
                        )
                    )


                    Spacer(modifier = Modifier.height(32.dp))


                    Button(
                        onClick = { viewModel.login() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.RojoP))
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = colorResource(R.color.rojoFlojito))
                        } else {
                            Text("Entrar", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White)
                        }
                    }


                    Spacer(modifier = Modifier.height(8.dp))


                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿No tienes cuenta? ",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        TextButton(
                            onClick = onNavigateToRegister,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Regístrate",
                                style = TextStyle(textDecoration = TextDecoration.Underline),
                                color = colorResource(id = R.color.rojoFlojito)
                            )
                        }
                    }
                }
            }
        }
    }


    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onLoginSuccess()
    }


    LaunchedEffect(uiState.authError) {
        uiState.authError?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
}