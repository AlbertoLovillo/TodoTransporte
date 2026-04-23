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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s25am.todotransporte.ui.screens.authentication.viewmodel.AuthenticationViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.s25am.todotransporte.R

@Composable
fun LoginScreen(
    viewModel: AuthenticationViewModel,
    onNavigateToRegister: () -> Unit, // Acción para ir a la pantalla de registro
    onLoginSuccess: () -> Unit       // Acción para navegar al mapa tras loguearse
) {
    // Obtenemos el estado desde el ViewModel para reaccionar a cambios
    val uiState by viewModel.uiState.collectAsState()

    // Observador de éxito: Cuando el ViewModel marca isSuccess como true, navegamos
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    // Contenedor principal de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Tarjeta contenedora del formulario de acceso
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

                // Imagen del logo circular
                Image(
                    painter = painterResource(id = R.drawable.logo_sinfondo),
                    contentDescription = "Logo TodoTransporte",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape) // Corte circular perfecto
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                // Nombre de la aplicación
                Text(
                    "TodoTransporte",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.RojoP)
                )

                Text(
                    "Inicia sesión para continuar",
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.rojoflojito)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input para el correo electrónico
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,   // Color cuando escribes
                        unfocusedTextColor = Color.Black, // Color cuando no está seleccionado
                        focusedLabelColor = Color.Black,  // Opcional: color del label al pinchar
                        cursorColor = Color.Black         // Opcional: color del palito que parpadea
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input para la contraseña con máscara de seguridad
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // Mensaje de error dinámico (solo aparece si authError no es nulo)
                uiState.authError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón de acceso principal
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading, // Se bloquea durante la carga
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.RojoP),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    // Muestra el indicador de carga o el texto según el estado
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Entrar")
                    }
                }

                // Botón secundario para usuarios sin cuenta
                TextButton(
                    onClick = onNavigateToRegister,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(id = R.color.rojoflojito)
                    )
                ) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate",
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline // Texto subrayado
                        )
                    )
                }
            }
        }
    }
}