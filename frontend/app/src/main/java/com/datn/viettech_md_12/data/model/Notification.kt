import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("_id")
    val id: String,
    val receiverId: String,
    val senderId: String?, // Có thể null
    val title: String,
    val message: String,
    val type: String,
    val url: String,
    val data: NotificationData,
    val isRead: Boolean,
    val createdAt: String,
    val updatedAt: String
)
data class NotificationData(
    val billId: String,
    val status: String
)
data class NotificationResponse(
    val notifications: List<NotificationModel>
)
