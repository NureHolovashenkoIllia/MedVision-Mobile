package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class UserResponse(
    val userId: Long,
    val userName: String,
    val email: String,
    val userRole: String,
    val creationDatetime: String,
    val avatarUrl: String?
)