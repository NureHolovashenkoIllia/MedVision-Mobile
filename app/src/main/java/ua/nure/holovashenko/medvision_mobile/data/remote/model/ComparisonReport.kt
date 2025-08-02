package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class ComparisonReport(
    val fromId: Long,
    val toId: Long,
    val analysisDetailsFrom: String?,
    val analysisDetailsTo: String?,
    val diagnosisTextFrom: String?,
    val diagnosisTextTo: String?,
    val treatmentRecommendationsFrom: String?,
    val treatmentRecommendationsTo: String?,
    val diagnosisClassFrom: Int,
    val diagnosisClassTo: Int,
    val fromImageBase64: String,
    val toImageBase64: String,
    val diffHeatmap: String,
    val accuracyFrom: Float?,
    val accuracyTo: Float?,
    val precisionFrom: Float?,
    val precisionTo: Float?,
    val recallFrom: Float?,
    val recallTo: Float?,
    val createdAtFrom: String,
    val createdAtTo: String
)