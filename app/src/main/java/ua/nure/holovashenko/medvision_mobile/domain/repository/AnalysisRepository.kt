package ua.nure.holovashenko.medvision_mobile.domain.repository

import ua.nure.holovashenko.medvision_mobile.data.remote.model.ComparisonReport
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.UpdateStatusRequest

interface AnalysisRepository {
    suspend fun getAnalysesByPatient(patientId: Long): Result<List<ImageAnalysisResponse>>
    suspend fun getAnalysisById(id: Long): Result<ImageAnalysisResponse>
    suspend fun compareAnalyses(fromId: Long, toId: Long): Result<ComparisonReport>
    suspend fun downloadComparisonPdf(fromId: Long, toId: Long): Result<ByteArray>
    suspend fun updateAnalysisStatus(id: Long, status: UpdateStatusRequest): Result<Unit>
}