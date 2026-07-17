package com.maxton.bad_android.features.admin.data.repositories

import com.maxton.bad_android.features.admin.domain.entities.*
import com.maxton.bad_android.features.admin.domain.repositories.AdminRepository
import java.util.Date

class AdminRepositoryImpl : AdminRepository {
    private val members = mutableListOf(
        MemberItem("m1", "Trần Nguyễn Tiến", "tientran", null, "admin", "Advanced", false),
        MemberItem("m2", "Nguyễn Thức Phúc", "phucnguyen", null, "admin", "Advanced", false),
        MemberItem("m3", "Lê Hoài Nam", "namle", null, "member", "Intermediate", false),
        MemberItem("m4", "Phạm Minh Trí", "tripham", null, "member", "Beginner", false),
        MemberItem("m5", "Hoàng Kim Chi", "chinhoang", null, "member", "Intermediate", true)
    )

    private val joinRequests = mutableListOf(
        JoinRequestItem(
            id = "r1",
            fullName = "Đặng Tuấn Anh",
            username = "anhdang",
            avatarUrl = null,
            skillLevel = "Advanced",
            message = "Xin chào, mình đánh được 3 năm rồi, muốn tìm nhóm giao lưu nâng cao trình độ vào cuối tuần!",
            requestedAt = Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000) // 3 hours ago
        ),
        JoinRequestItem(
            id = "r2",
            fullName = "Vũ Thu Trang",
            username = "trangvu",
            avatarUrl = null,
            skillLevel = "Beginner",
            message = "Mình mới tập chơi được 3 tháng, muốn tìm nhóm vui vẻ để rèn luyện sức khoẻ ạ.",
            requestedAt = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000) // 2 days ago
        )
    )

    override suspend fun getMembers(): Result<List<MemberItem>> {
        return Result.success(members.toList())
    }

    override suspend fun getJoinRequests(): Result<List<JoinRequestItem>> {
        return Result.success(joinRequests.toList())
    }

    override suspend fun approveRequest(requestId: String): Result<Unit> {
        val request = joinRequests.firstOrNull { it.id == requestId }
        if (request != null) {
            joinRequests.remove(request)
            members.add(MemberItem(
                id = "m-${System.currentTimeMillis()}",
                fullName = request.fullName,
                username = request.username,
                avatarUrl = request.avatarUrl,
                role = "member",
                skillLevel = request.skillLevel,
                isBlocked = false
            ))
        }
        return Result.success(Unit)
    }

    override suspend fun rejectRequest(requestId: String): Result<Unit> {
        joinRequests.removeIf { it.id == requestId }
        return Result.success(Unit)
    }

    override suspend fun changeMemberRole(memberId: String, newRole: String): Result<Unit> {
        val index = members.indexOfFirst { it.id == memberId }
        if (index != -1) {
            members[index] = members[index].copy(role = newRole)
        }
        return Result.success(Unit)
    }

    override suspend fun toggleBlockMember(memberId: String): Result<Unit> {
        val index = members.indexOfFirst { it.id == memberId }
        if (index != -1) {
            members[index] = members[index].copy(isBlocked = !members[index].isBlocked)
        }
        return Result.success(Unit)
    }
}
