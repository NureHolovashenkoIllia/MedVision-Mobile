package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class DiagnosisHistoryResponse(
    val id: Long,
    val diagnosisText: String,
    val doctorName: String?,
    val reason: String,
    val timestamp: String,
    val analysisDetails: String?,
    val treatmentRecommendations: String?
)