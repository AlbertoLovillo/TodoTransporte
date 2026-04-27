package com.s25am.todotransporte.ui.screens.authentication.viewmodel

data class AuthenticationUiState(
    val email: String = "",
    val nombre: String = "ejemplo", // TODO: implementar esto en alguna pantalla
    val password: String = "",
    val repeatedPassword: String = "",
    val isLoading: Boolean = false,
    val authError: String? = null,
    val isSuccess: Boolean = false
)
