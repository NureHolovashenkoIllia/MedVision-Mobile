package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class ImageFileResponse(
    val imageFileId: Long,
    val imageFileUrl: String,
    val imageFileName: String,
    val imageFileType: String,
    val uploadedAt: String,
    val uploadedBy: UserResponse
)