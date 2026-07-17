package com.maxton.bad_android.features.match.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.features.match.domain.entities.MatchItem
import com.maxton.bad_android.features.match.domain.entities.OpponentCandidate
import com.maxton.bad_android.features.match.domain.repositories.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface MatchUiState {
    data object Loading : MatchUiState
    data class Success(
        val scheduledMatches: List<MatchItem>,
        val completedMatches: List<MatchItem>,
        val opponents: List<OpponentCandidate> = emptyList(),
    ) : MatchUiState
    data class Error(val message: String) : MatchUiState
}

sealed interface MatchmakingUiState {
    data object Loading : MatchmakingUiState
    data class Success(val candidates: List<OpponentCandidate>) : MatchmakingUiState
    data class Error(val message: String) : MatchmakingUiState
}

sealed interface MatchDetailUiState {
    data object Loading : MatchDetailUiState
    data class Success(val match: MatchItem) : MatchDetailUiState
    data class Error(val message: String) : MatchDetailUiState
}

class MatchViewModel(
    private val matchRepository: MatchRepository = ServiceLocator.matchRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MatchUiState>(MatchUiState.Loading)
    val uiState: StateFlow<MatchUiState> = _uiState

    private val _matchmakingState = MutableStateFlow<MatchmakingUiState>(MatchmakingUiState.Loading)
    val matchmakingState: StateFlow<MatchmakingUiState> = _matchmakingState

    private val _matchDetailState = MutableStateFlow<MatchDetailUiState>(MatchDetailUiState.Loading)
    val matchDetailState: StateFlow<MatchDetailUiState> = _matchDetailState

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    init {
        loadMatchData()
    }

    fun loadMatchData() {
        viewModelScope.launch {
            _uiState.value = MatchUiState.Loading
            val scheduledRes = matchRepository.getScheduledMatches()
            val completedRes = matchRepository.getCompletedMatches()
            val opponentsRes = matchRepository.findMatchmakingOpponents()

            if (scheduledRes.isSuccess && completedRes.isSuccess) {
                _uiState.value = MatchUiState.Success(
                    scheduledMatches = scheduledRes.getOrThrow(),
                    completedMatches = completedRes.getOrThrow(),
                    opponents = opponentsRes.getOrDefault(emptyList()),
                )
            } else {
                _uiState.value = MatchUiState.Error("Failed to load match listings")
            }
        }
    }

    fun loadMatchmakingCandidates() {
        viewModelScope.launch {
            _matchmakingState.value = MatchmakingUiState.Loading
            matchRepository.findMatchmakingOpponents()
                .onSuccess { _matchmakingState.value = MatchmakingUiState.Success(it) }
                .onFailure {
                    _matchmakingState.value =
                        MatchmakingUiState.Error(it.message ?: "Failed to load opponents")
                }
        }
    }

    fun loadMatchDetail(matchId: String) {
        viewModelScope.launch {
            _matchDetailState.value = MatchDetailUiState.Loading
            matchRepository.getMatchDetail(matchId)
                .onSuccess { _matchDetailState.value = MatchDetailUiState.Success(it) }
                .onFailure {
                    _matchDetailState.value =
                        MatchDetailUiState.Error(it.message ?: "Failed to load match")
                }
        }
    }

    fun createMatch(
        opponentId: String,
        courtName: String,
        dateTimeMillis: Long,
        onSuccess: (MatchItem) -> Unit,
        onError: (String) -> Unit = {},
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            matchRepository.createMatch(opponentId, courtName, dateTimeMillis)
                .onSuccess { match ->
                    loadMatchData()
                    onSuccess(match)
                }
                .onFailure { onError(it.message ?: "Failed to create match") }
            _isSubmitting.value = false
        }
    }

    fun enterMatchScore(
        matchId: String,
        setScores: List<Map<String, Int>>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {},
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            matchRepository.enterMatchScore(matchId, setScores)
                .onSuccess {
                    loadMatchData()
                    onSuccess()
                }
                .onFailure { onError(it.message ?: "Failed to save score") }
            _isSubmitting.value = false
        }
    }
}
