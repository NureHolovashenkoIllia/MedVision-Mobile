package ua.nure.holovashenko.medvision_mobile.data.repository

import ua.nure.holovashenko.medvision_mobile.data.remote.datasource.AnalysisRemoteDataSource
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
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
}
