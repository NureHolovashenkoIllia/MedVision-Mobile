package ua.nure.holovashenko.medvision_mobile.domain.repository

import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse

interface PatientRepository {
    suspend fun getMyAnalyses(): Result<List<ImageAnalysisResponse>>
    suspend fun getUnviewedAnalyses(): Result<List<ImageAnalysisResponse>>
    suspend fun getAnalysis(id: Long): Result<ImageAnalysisResponse>
    suspend fun getHeatmap(id: Long): Result<ByteArray>
    suspend fun getAllHeatmaps(): Result<Map<Long, ByteArray>>
    suspend fun getImage(id: Long): Result<ByteArray>
    suspend fun getAllImages(): Result<Map<Long, ByteArray>>
    suspend fun exportAnalysisToPdf(id: Long): Result<ByteArray>
}