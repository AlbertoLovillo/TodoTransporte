package com.s25am.todotransporte.ui.screens.authentication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class AuthenticationViewModel : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun updateNombre(value: String) {
        _uiState.update { it.copy(nombre = value) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun updateRepeatedPassword(value: String) {
        _uiState.update { it.copy(repeatedPassword = value) }
    }

    fun register() {
        Log.d(TAG, "register: Iniciando proceso de registro...")

        if (_uiState.value.password != _uiState.value.repeatedPassword) {
            Log.w(TAG, "register: Las contraseñas no coinciden. Abortando.")
            _uiState.update { it.copy(authError = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {

                val existingUsers = SupabaseClient.client.from("Usuario")
                    .select {
                        filter {
                            eq("email", _uiState.value.email)
                        }
                    }.data

                if (existingUsers != "[]") {
                    Log.w(TAG, "register: El correo ya existe en la base de datos.")
                    _uiState.update {
                        it.copy(
                            authError = "Ya existe una cuenta con este correo electrónico.",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                Log.d(
                    TAG,
                    "register: Llamando a Supabase signUpWith para email: ${_uiState.value.email}"
                )

                SupabaseClient.client.auth.signUpWith(Email) {
                    email = _uiState.value.email
                    password = _uiState.value.password
                    data = buildJsonObject {
                        put("nombre", _uiState.value.nombre)
                    }
                }

                Log.d(TAG, "register: ¡Éxito! Usuario creado en Supabase.")
                _uiState.update { it.copy(isSuccess = true, authError = "¡Registro exitoso!") }

            } catch (e: Exception) {
                Log.e(TAG, "register: Error crítico al registrar usuario: ${e.message}", e)

                val errorMsg = e.message ?: ""
                val errorFriendly = when {
                    errorMsg.contains("already registered", ignoreCase = true) ||
                            errorMsg.contains("already exists", ignoreCase = true) ->
                        "Ya existe una cuenta con este correo electrónico."

                    errorMsg.contains("weak_password", ignoreCase = true) ||
                            errorMsg.contains("Password should be at least", ignoreCase = true) ->
                        "La contraseña es demasiado débil (mínimo 6 caracteres)."

                    errorMsg.contains("validation_failed", ignoreCase = true) ->
                        "Los datos introducidos no son válidos."

                    errorMsg.contains("email_address_invalid", ignoreCase = true) ||
                            errorMsg.contains("Unable to validate email", ignoreCase = true) ->
                        "El formato del correo electrónico no es válido."

                    errorMsg.contains("rate_limit", ignoreCase = true) ||
                            errorMsg.contains("over_email_send", ignoreCase = true) ->
                        "Demasiados intentos. Espera unos minutos y vuelve a intentarlo."

                    else -> "Ocurrió un error inesperado al registrar la cuenta."
                }

                _uiState.update { it.copy(authError = errorFriendly, isSuccess = false) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun login() {
        Log.d(TAG, "login: Iniciando proceso de login...")

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            Log.w(TAG, "login: Formato de email inválido. Abortando.")
            _uiState.update { it.copy(authError = "El formato del email no es válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                Log.d(TAG, "login: Llamando a Supabase signInWith para email: ${_uiState.value.email}")

                SupabaseClient.client.auth.signInWith(Email) {
                    email = _uiState.value.email
                    password = _uiState.value.password
                }

                Log.d(TAG, "login: ¡Éxito! Sesión iniciada.")
                _uiState.update { it.copy(isSuccess = true, authError = null) }

            } catch (e: Exception) {
                Log.e(TAG, "login: Error crítico al iniciar sesión: ${e.message}", e)

                val errorMsg = e.message ?: ""
                val errorFriendly = when {
                    errorMsg.contains("invalid_credentials", ignoreCase = true) ||
                            errorMsg.contains("Invalid login credentials", ignoreCase = true) ->
                        "Email o contraseña incorrectos."

                    errorMsg.contains("email_not_confirmed", ignoreCase = true) ||
                            errorMsg.contains("Email not confirmed", ignoreCase = true) ->
                        "Debes confirmar tu correo electrónico antes de entrar."

                    else -> "Error al iniciar sesión. Comprueba tus datos."
                }

                _uiState.update { it.copy(authError = errorFriendly, isSuccess = false) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun clearError() {
        _uiState.update { it.copy(authError = null) }
    }
}