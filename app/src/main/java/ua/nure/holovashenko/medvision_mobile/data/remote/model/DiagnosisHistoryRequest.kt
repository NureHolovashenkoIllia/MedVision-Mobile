package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class DiagnosisHistoryRequest(
    val analysisId: Long,
    val diagnosisText: String,
    val doctorId: Long?,
    val reason: String,
    val analysisDetails: String,
    val treatmentRecommendations: String
)