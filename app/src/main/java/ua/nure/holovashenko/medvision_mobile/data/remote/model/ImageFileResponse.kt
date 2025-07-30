package ua.nure.holovashenko.medvision_mobile.data.remote.model

import java.time.LocalDateTime

data class ImageFileResponse(
    val imageFileId: Long,
    val imageFileUrl: String,
    val imageFileName: String,
    val imageFileType: String,
    val uploadedAt: LocalDateTime,
    val uploadedBy: UserResponse
)