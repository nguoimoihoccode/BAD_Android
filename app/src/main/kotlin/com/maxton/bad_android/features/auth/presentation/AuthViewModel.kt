package com.maxton.bad_android.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.core.network.SessionExpiredBus
import com.maxton.bad_android.features.auth.domain.entities.User
import com.maxton.bad_android.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Initial : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        viewModelScope.launch {
            SessionExpiredBus.events.collect {
                logout()
            }
        }
        restoreSession()
    }

    private fun restoreSession() {
        viewModelScope.launch {
            if (!authRepository.isAuthenticated()) {
                _uiState.value = AuthUiState.Initial
                _isAuthenticated.value = false
                return@launch
            }
            _uiState.value = AuthUiState.Loading
            authRepository.getMe().fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Success(it)
                    _isAuthenticated.value = true
                },
                onFailure = {
                    authRepository.logout()
                    _uiState.value = AuthUiState.Initial
                    _isAuthenticated.value = false
                },
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(email, password).fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Success(it)
                    _isAuthenticated.value = true
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Login failed")
                    _isAuthenticated.value = false
                },
            )
        }
    }

    fun register(email: String, username: String, password: String, fullName: String?) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(email, username, password, fullName).fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Success(it)
                    _isAuthenticated.value = true
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Registration failed")
                    _isAuthenticated.value = false
                },
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState.Initial
            _isAuthenticated.value = false
        }
    }
}
