package ua.nure.holovashenko.medvision_mobile.data.repository

import ua.nure.holovashenko.medvision_mobile.data.remote.datasource.AnalysisRemoteDataSource
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ComparisonReport
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.UpdateStatusRequest
import ua.nure.holovashenko.medvision_mobile.domain.repository.AnalysisRepository
import javax.inject.Inject

class AnalysisRepositoryImpl @Inject constructor(
    private val remote: AnalysisRemoteDataSource
) : AnalysisRepository {
    override suspend fun getAnalysesByPatient(patientId: Long): Result<List<ImageAnalysisResponse>> = runCatching {
        val response = remote.getAnalysesByPatient(patientId)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Не вдалося отримати аналізи")
    }

    override suspend fun getAnalysisById(id: Long): Result<ImageAnalysisResponse> = runCatching {
        val response = remote.getAnalysisById(id)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Аналіз не знайдено")
    }

    override suspend fun compareAnalyses(fromId: Long, toId: Long): Result<ComparisonReport> = runCatching {
        val response = remote.compareAnalyses(fromId, toId)
        if (response.isSuccessful) response.body() ?: throw Exception("Порожній звіт")
        else throw Exception("Помилка порівняння: ${response.code()}")
    }

    override suspend fun downloadComparisonPdf(fromId: Long, toId: Long): Result<ByteArray> = runCatching {
        val response = remote.downloadComparisonPdf(fromId, toId)
        if (response.isSuccessful) response.body()!!.bytes()
        else throw Exception("Помилка PDF: ${response.code()}")
    }

    override suspend fun updateAnalysisStatus(id: Long, status: UpdateStatusRequest): Result<Unit> =
        runCatching {
            val response = remote.updateAnalysisStatus(id, status)
            if (!response.isSuccessful) throw Exception("Не вдалося оновити статус аналізу")
        }
}