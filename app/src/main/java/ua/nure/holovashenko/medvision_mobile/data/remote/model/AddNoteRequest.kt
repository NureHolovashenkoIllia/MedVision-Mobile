package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class AddNoteRequest(
    val noteText: String,
    val noteAreaX: Int?,
    val noteAreaY: Int?,
    val noteAreaWidth: Int?,
    val noteAreaHeight: Int?
)