package ua.nure.holovashenko.medvision_mobile.domain.repository

import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse

interface AnalysisRepository {
    suspend fun getAnalysesByPatient(patientId: Long): Result<List<ImageAnalysisResponse>>
    suspend fun getAnalysisById(id: Long): Result<ImageAnalysisResponse>
}
