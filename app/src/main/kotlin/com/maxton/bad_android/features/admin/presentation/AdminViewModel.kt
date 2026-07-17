package com.maxton.bad_android.features.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.features.admin.domain.entities.JoinRequestItem
import com.maxton.bad_android.features.admin.domain.entities.MemberItem
import com.maxton.bad_android.features.admin.domain.repositories.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AdminUiState {
    object Loading : AdminUiState
    data class Success(
        val members: List<MemberItem>,
        val requests: List<JoinRequestItem>
    ) : AdminUiState
    data class Error(val message: String) : AdminUiState
}

class AdminViewModel(
    private val adminRepository: AdminRepository = ServiceLocator.adminRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState: StateFlow<AdminUiState> = _uiState

    init {
        loadAdminData()
    }

    fun loadAdminData() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            val membersRes = adminRepository.getMembers()
            val requestsRes = adminRepository.getJoinRequests()

            if (membersRes.isSuccess && requestsRes.isSuccess) {
                _uiState.value = AdminUiState.Success(membersRes.getOrThrow(), requestsRes.getOrThrow())
            } else {
                _uiState.value = AdminUiState.Error("Failed to fetch admin console items")
            }
        }
    }

    fun approveRequest(requestId: String) {
        viewModelScope.launch {
            adminRepository.approveRequest(requestId).onSuccess {
                loadAdminData()
            }
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            adminRepository.rejectRequest(requestId).onSuccess {
                loadAdminData()
            }
        }
    }

    fun changeMemberRole(memberId: String, newRole: String) {
        viewModelScope.launch {
            adminRepository.changeMemberRole(memberId, newRole).onSuccess {
                loadAdminData()
            }
        }
    }

    fun toggleBlockMember(memberId: String) {
        viewModelScope.launch {
            adminRepository.toggleBlockMember(memberId).onSuccess {
                loadAdminData()
            }
        }
    }
}
