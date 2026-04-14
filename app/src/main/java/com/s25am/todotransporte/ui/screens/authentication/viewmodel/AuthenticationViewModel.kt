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
    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun updateRepeatedPassword(value: String) {
        _uiState.update { it.copy(repeatedPassword = value) }
    }


    fun register() {
        if (_uiState.value.password != _uiState.value.repeatedPassword) {
            _uiState.update { it.copy(authError = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    email = _uiState.value.email
                    password = _uiState.value.password
                }

                _uiState.update { it.copy(authError = "¡Registro exitoso! Revisa tu correo.") }

            } catch (e: Exception) {
                _uiState.update { it.copy(authError = e.message) }

            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun login() {
        if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.update { it.copy(authError = "Por favor, completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    email = _uiState.value.email
                    password = _uiState.value.password
                }

                // TODO: navegacion a la pantalla Maps (principal)
                _uiState.update { it.copy(authError = "¡Login exitoso!") }

            } catch (e: Exception) {
                _uiState.update { it.copy(authError = e.message) }

            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}