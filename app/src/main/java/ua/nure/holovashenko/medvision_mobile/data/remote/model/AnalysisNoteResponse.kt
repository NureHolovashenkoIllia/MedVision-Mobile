package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class AnalysisNoteResponse(
    val analysisNoteId: Long,
    val noteText: String,

    val noteAreaX: Int,
    val noteAreaY: Int,
    val noteAreaWidth: Int,
    val noteAreaHeight: Int,

    val creationDatetime: String,

    val imageAnalysisId: Long,
    val imageFileId: Long,
    val heatmapFileId: Long,
    val doctorId: Long,
)