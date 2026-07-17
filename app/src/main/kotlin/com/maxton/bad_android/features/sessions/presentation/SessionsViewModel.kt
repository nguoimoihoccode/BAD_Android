package com.maxton.bad_android.features.sessions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.features.sessions.domain.entities.Session
import com.maxton.bad_android.features.sessions.domain.repositories.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SessionsUiState {
    object Loading : SessionsUiState
    data class Success(val upcoming: List<Session>, val past: List<Session>) : SessionsUiState
    data class Error(val message: String) : SessionsUiState
}

sealed interface SessionDetailsUiState {
    object Loading : SessionDetailsUiState
    data class Success(val session: Session) : SessionDetailsUiState
    data class Error(val message: String) : SessionDetailsUiState
}

class SessionsViewModel(
    private val sessionRepository: SessionRepository = ServiceLocator.sessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SessionsUiState>(SessionsUiState.Loading)
    val uiState: StateFlow<SessionsUiState> = _uiState

    private val _detailsUiState = MutableStateFlow<SessionDetailsUiState>(SessionDetailsUiState.Loading)
    val detailsUiState: StateFlow<SessionDetailsUiState> = _detailsUiState

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = SessionsUiState.Loading
            val upcomingResult = sessionRepository.getUpcomingSessions()
            val pastResult = sessionRepository.getPastSessions()

            if (upcomingResult.isSuccess && pastResult.isSuccess) {
                _uiState.value = SessionsUiState.Success(
                    upcomingResult.getOrThrow(),
                    pastResult.getOrThrow()
                )
            } else {
                val errorMsg = upcomingResult.exceptionOrNull()?.message ?: pastResult.exceptionOrNull()?.message ?: "Unknown error"
                _uiState.value = SessionsUiState.Error(errorMsg)
            }
        }
    }

    fun loadSessionDetails(sessionId: String) {
        viewModelScope.launch {
            _detailsUiState.value = SessionDetailsUiState.Loading
            sessionRepository.getSessionDetails(sessionId).fold(
                onSuccess = { _detailsUiState.value = SessionDetailsUiState.Success(it) },
                onFailure = { _detailsUiState.value = SessionDetailsUiState.Error(it.message ?: "Failed to load details") }
            )
        }
    }

    fun toggleJoinSession(sessionId: String, isJoined: Boolean) {
        viewModelScope.launch {
            _detailsUiState.value = SessionDetailsUiState.Loading
            val result = if (isJoined) {
                sessionRepository.leaveSession(sessionId)
            } else {
                sessionRepository.joinSession(sessionId)
            }
            result.fold(
                onSuccess = {
                    _detailsUiState.value = SessionDetailsUiState.Success(it)
                    loadSessions()
                },
                onFailure = { _detailsUiState.value = SessionDetailsUiState.Error(it.message ?: "Action failed") }
            )
        }
    }
}
