package com.maxton.bad_android.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.features.notifications.domain.entities.NotificationItem
import com.maxton.bad_android.features.notifications.domain.repositories.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface NotificationsUiState {
    object Loading : NotificationsUiState
    data class Success(val notifications: List<NotificationItem>) : NotificationsUiState
    data class Error(val message: String) : NotificationsUiState
}

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository = ServiceLocator.notificationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationsUiState.Loading
            notificationRepository.getNotifications().fold(
                onSuccess = { _uiState.value = NotificationsUiState.Success(it) },
                onFailure = { _uiState.value = NotificationsUiState.Error(it.message ?: "Failed to load notifications") }
            )
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId).onSuccess {
                loadNotifications()
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead().onSuccess {
                loadNotifications()
            }
        }
    }
}
