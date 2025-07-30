package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class ImageAnalysisResponse(
    val imageAnalysisId: Long,
    val analysisAccuracy: Float?,
    val analysisPrecision: Float?,
    val analysisRecall: Float?,
    val analysisDetails: String?,
    val analysisDiagnosis: String?,
    val treatmentRecommendations: String?,
    val creationDatetime: String,
    val analysisStatus: String,
    val viewed: Boolean,
    val diagnosisClass: Int?,
    val imageFile: ImageFileResponse?,
    val heatmapFile: ImageFileResponse?,
    val patient: UserResponse,
    val doctor: UserResponse
)