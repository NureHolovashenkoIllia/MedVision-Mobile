package ua.nure.holovashenko.medvision_mobile.data.remote.datasource

import retrofit2.Response
import ua.nure.holovashenko.medvision_mobile.data.remote.api.PatientApi
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import okhttp3.ResponseBody
import javax.inject.Inject

class PatientRemoteDataSource @Inject constructor(
    private val api: PatientApi
) {
    suspend fun getMyAnalyses(): Response<List<ImageAnalysisResponse>> =
        api.getMyAnalyses()

    suspend fun getUnviewedAnalyses(): Response<List<ImageAnalysisResponse>> =
        api.getUnviewedAnalyses()

    suspend fun getAnalysis(id: Long): Response<ImageAnalysisResponse> =
        api.getAnalysis(id)

    suspend fun getAllHeatmaps(): Response<Map<String, String>> =
        api.getAllHeatmaps()

    suspend fun getAllImages(): Response<Map<String, String>> =
        api.getAllImages()

    suspend fun exportAnalysisToPdf(id: Long): Response<ResponseBody> =
        api.exportAnalysisToPdf(id)
}