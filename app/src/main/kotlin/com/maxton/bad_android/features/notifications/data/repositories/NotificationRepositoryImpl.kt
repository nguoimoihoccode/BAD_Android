package com.maxton.bad_android.features.notifications.data.repositories

import com.maxton.bad_android.features.notifications.domain.entities.NotificationItem
import com.maxton.bad_android.features.notifications.domain.repositories.NotificationRepository
import java.util.Date

class NotificationRepositoryImpl : NotificationRepository {
    private val notifications = mutableListOf(
        NotificationItem(
            id = "n1",
            title = "Sân số 3 đã được đặt thành công",
            body = "Buổi tập lúc 18:00 ngày mai tại sân cỏ nhân tạo đã được ghi nhận. Chuẩn bị ra sân nhé!",
            createdAt = Date(System.currentTimeMillis() - 10 * 60 * 1000),
            isRead = false,
            type = "booking"
        ),
        NotificationItem(
            id = "n2",
            title = "Có thông báo mới từ ban quản trị",
            body = "Lịch thi đấu đơn nam/nữ giải Maxton Open đã được công bố. Vui lòng kiểm tra tab Cộng đồng để biết thêm chi tiết.",
            createdAt = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000),
            isRead = false,
            type = "announcement"
        ),
        NotificationItem(
            id = "n3",
            title = "Yêu cầu thanh toán chi phí buổi tập",
            body = "Hoá đơn chi phí buổi tập ngày 05/07 đang chờ xử lý. Vui lòng thanh toán số tiền 120,000đ.",
            createdAt = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000),
            isRead = true,
            type = "payment"
        ),
        NotificationItem(
            id = "n4",
            title = "Xác nhận thành viên mới gia nhập",
            body = "Thành viên Nguyễn Văn A vừa được phê duyệt tham gia câu lạc bộ.",
            createdAt = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000),
            isRead = true,
            type = "announcement"
        )
    )

    override suspend fun getNotifications(): Result<List<NotificationItem>> {
        return Result.success(notifications.toList())
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        val index = notifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            notifications[index] = notifications[index].copy(isRead = true)
        }
        return Result.success(Unit)
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        for (i in 0 until notifications.size) {
            notifications[i] = notifications[i].copy(isRead = true)
        }
        return Result.success(Unit)
    }
}
