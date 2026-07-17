package com.maxton.bad_android.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.features.profile.domain.entities.PlayerProfile
import com.maxton.bad_android.features.profile.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface PlayerProfileUiState {
    data object Loading : PlayerProfileUiState
    data class ProfileLoaded(val profile: PlayerProfile) : PlayerProfileUiState
    data class LeaderboardLoaded(val leaderboard: List<PlayerProfile>) : PlayerProfileUiState
    data object RatingSuccess : PlayerProfileUiState
    data class Error(val message: String) : PlayerProfileUiState
}

class PlayerProfileViewModel(
    private val playerRepository: PlayerRepository = ServiceLocator.playerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<PlayerProfileUiState>(PlayerProfileUiState.Loading)
    val uiState: StateFlow<PlayerProfileUiState> = _uiState

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    fun loadPlayerProfile(playerId: String) {
        viewModelScope.launch {
            _uiState.value = PlayerProfileUiState.Loading
            playerRepository.getPlayerProfile(playerId)
                .onSuccess { _uiState.value = PlayerProfileUiState.ProfileLoaded(it) }
                .onFailure {
                    _uiState.value =
                        PlayerProfileUiState.Error(it.message ?: "Failed to load profile")
                }
        }
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = PlayerProfileUiState.Loading
            playerRepository.getLeaderboard()
                .onSuccess { _uiState.value = PlayerProfileUiState.LeaderboardLoaded(it) }
                .onFailure {
                    _uiState.value =
                        PlayerProfileUiState.Error(it.message ?: "Failed to load leaderboard")
                }
        }
    }

    fun submitPlayerRating(playerId: String, skillRating: Double, fairPlayRating: Double) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _uiState.value = PlayerProfileUiState.Loading
            playerRepository.ratePlayer(playerId, skillRating, fairPlayRating)
                .onSuccess { _uiState.value = PlayerProfileUiState.RatingSuccess }
                .onFailure {
                    _uiState.value =
                        PlayerProfileUiState.Error(it.message ?: "Failed to submit rating")
                }
            _isSubmitting.value = false
        }
    }
}
