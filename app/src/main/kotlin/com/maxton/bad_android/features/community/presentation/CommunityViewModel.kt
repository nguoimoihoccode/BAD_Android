package com.maxton.bad_android.features.community.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.features.community.domain.entities.*
import com.maxton.bad_android.features.community.domain.repositories.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface CommunityUiState {
    object Loading : CommunityUiState
    data class Success(
        val announcements: List<CommunityAnnouncement>,
        val activeMembers: List<CommunityMember>,
        val topPlayers: List<CommunityMember>,
        val poll: CommunityPoll,
        val messages: List<ChatMessage>
    ) : CommunityUiState
    data class Error(val message: String) : CommunityUiState
}

class CommunityViewModel(
    private val communityRepository: CommunityRepository = ServiceLocator.communityRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CommunityUiState>(CommunityUiState.Loading)
    val uiState: StateFlow<CommunityUiState> = _uiState

    init {
        loadCommunityData()
    }

    fun loadCommunityData() {
        viewModelScope.launch {
            _uiState.value = CommunityUiState.Loading
            val announcementsRes = communityRepository.getAnnouncements()
            val activeRes = communityRepository.getActiveMembers()
            val topRes = communityRepository.getTopPlayers()
            val pollRes = communityRepository.getPoll()
            val messagesRes = communityRepository.getRecentMessages()

            if (announcementsRes.isSuccess && activeRes.isSuccess && topRes.isSuccess && pollRes.isSuccess && messagesRes.isSuccess) {
                _uiState.value = CommunityUiState.Success(
                    announcementsRes.getOrThrow(),
                    activeRes.getOrThrow(),
                    topRes.getOrThrow(),
                    pollRes.getOrThrow(),
                    messagesRes.getOrThrow()
                )
            } else {
                _uiState.value = CommunityUiState.Error("Failed to load community details")
            }
        }
    }

    fun voteInPoll(optionId: String) {
        viewModelScope.launch {
            communityRepository.voteInPoll(optionId).onSuccess {
                loadCommunityData()
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            communityRepository.sendChatMessage(text).onSuccess {
                loadCommunityData()
            }
        }
    }
}
