package com.maxton.bad_android.features.admin.domain.repositories

import com.maxton.bad_android.features.admin.domain.entities.*

interface AdminRepository {
    suspend fun getMembers(): Result<List<MemberItem>>
    suspend fun getJoinRequests(): Result<List<JoinRequestItem>>
    suspend fun approveRequest(requestId: String): Result<Unit>
    suspend fun rejectRequest(requestId: String): Result<Unit>
    suspend fun changeMemberRole(memberId: String, newRole: String): Result<Unit>
    suspend fun toggleBlockMember(memberId: String): Result<Unit>
}
