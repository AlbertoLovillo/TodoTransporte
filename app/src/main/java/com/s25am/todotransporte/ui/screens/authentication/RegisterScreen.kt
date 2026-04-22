package com.s25am.todotransporte.ui.screens.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.ui.screens.authentication.viewmodel.AuthenticationViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthenticationViewModel,
    onNavigateToLogin: () -> Unit,    // Función para volver al Login
    onRegisterSuccess: () -> Unit    // Función para ir al Mapa tras el éxito
) {
    // Observamos el estado actual desde el ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // "Vigilante": Si el registro es exitoso en el ViewModel, dispara la navegación
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    // Contenedor principal que ocupa toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Tarjeta blanca central que contiene el formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Logo de la empresa con recorte circular
                Image(
                    painter = painterResource(id = R.drawable.logo_sinfondo),
                    contentDescription = "Logo TodoTransporte",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )

                // Título principal con el color rojo de la marca
                Text(
                    "Registro",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.RojoP)
                )

                Text(
                    "Crea tu cuenta de transporte",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.rojoflojito)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo para introducir el correo electrónico
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo para la contraseña (oculta el texto)
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo para confirmar la contraseña
                OutlinedTextField(
                    value = uiState.repeatedPassword,
                    onValueChange = { viewModel.updateRepeatedPassword(it) },
                    label = { Text("Repetir Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Si hay un error (ej: contraseñas no coinciden), se muestra aquí
                uiState.authError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón principal de registro
                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading, // Se deshabilita mientras carga
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.RojoP),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    // Si está cargando, muestra un círculo; si no, el texto
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Crear Cuenta")
                    }
                }

                // Enlace subrayado para volver a la pantalla de Login
                TextButton(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(id = R.color.rojoflojito)
                    )
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
        }
    }
}