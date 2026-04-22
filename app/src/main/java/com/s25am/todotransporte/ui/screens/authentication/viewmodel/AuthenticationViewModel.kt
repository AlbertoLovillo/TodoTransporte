package com.s25am.todotransporte.ui.screens.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel(){

    // Estado privado (mutable) que solo el ViewModel puede modificar
    private val _uiState = MutableStateFlow(AuthenticationUiState())
    // Estado público (de solo lectura) que la UI observa
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()

    // Actualiza el email en el estado cada vez que el usuario escribe
    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    // Actualiza la contraseña en el estado
    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    // Actualiza la repetición de contraseña (para la pantalla de registro)
    fun updateRepeatedPassword(value: String) {
        _uiState.update { it.copy(repeatedPassword = value) }
    }

    /**
     * Lógica para registrar un nuevo usuario en Supabase
     */
    fun register() {
        // Validación local: Comprobar que las contraseñas coinciden antes de llamar al servidor
        if (_uiState.value.password != _uiState.value.repeatedPassword) {
            _uiState.update { it.copy(authError = "Las contraseñas no coinciden") }
            return
        }

        // Ejecutamos la tarea en una Corrutina para no bloquear la aplicación
        viewModelScope.launch {
            // Indicamos que estamos cargando y limpiamos errores previos
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                // Llamada a Supabase para crear el usuario
                SupabaseClient.client.auth.signUpWith(Email) {
                    email = _uiState.value.email
                    password = _uiState.value.password
                }

                // Si no hay error, marcamos éxito para que la UI navegue
                _uiState.update { it.copy(isSuccess = true, authError = "¡Registro exitoso!") }

            } catch (e: Exception) {
                // Si algo falla (ej: el email ya existe), guardamos el mensaje de error
                _uiState.update { it.copy(authError = e.message, isSuccess = false) }
            } finally {
                // Al terminar (bien o mal), quitamos el estado de carga
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Lógica para iniciar sesión con email y contraseña
     */
    fun login() {
        // Validación local: Verificar que el email tenga un formato correcto (ej: tenga un @)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            _uiState.update { it.copy(authError = "El formato del email no es válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                // Intento de conexión con los servidores de Supabase
                SupabaseClient.client.auth.signInWith(Email) {
                    email = _uiState.value.email
                    password = _uiState.value.password
                }

                // Si los datos son correctos, activamos la bandera de éxito
                _uiState.update { it.copy(isSuccess = true, authError = null) }

            } catch (e: Exception) {
                // Capturamos errores (ej: contraseña incorrecta o falta de internet)
                _uiState.update { it.copy(authError = e.message, isSuccess = false) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}