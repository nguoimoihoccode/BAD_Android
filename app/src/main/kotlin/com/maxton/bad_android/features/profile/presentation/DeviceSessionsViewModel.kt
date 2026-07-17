package com.maxton.bad_android.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.features.auth.domain.entities.DeviceSession
import com.maxton.bad_android.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface DeviceSessionsUiState {
    object Loading : DeviceSessionsUiState
    data class Success(val sessions: List<DeviceSession>) : DeviceSessionsUiState
    data class Error(val message: String) : DeviceSessionsUiState
}

class DeviceSessionsViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DeviceSessionsUiState>(DeviceSessionsUiState.Loading)
    val uiState: StateFlow<DeviceSessionsUiState> = _uiState

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = DeviceSessionsUiState.Loading
            authRepository.getDeviceSessions().fold(
                onSuccess = { _uiState.value = DeviceSessionsUiState.Success(it) },
                onFailure = { _uiState.value = DeviceSessionsUiState.Error(it.message ?: "Failed to load sessions") }
            )
        }
    }

    fun revokeSession(sessionId: String) {
        viewModelScope.launch {
            authRepository.revokeSession(sessionId).fold(
                onSuccess = { loadSessions() },
                onFailure = { _uiState.value = DeviceSessionsUiState.Error(it.message ?: "Failed to revoke session") }
            )
        }
    }
}
