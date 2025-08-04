package ua.nure.holovashenko.medvision_mobile.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse

interface PatientApi {
    @GET("/api/patient/analyses")
    suspend fun getMyAnalyses(): Response<List<ImageAnalysisResponse>>

    @GET("/api/patient/analyses/unviewed")
    suspend fun getUnviewedAnalyses(): Response<List<ImageAnalysisResponse>>

    @GET("/api/patient/analyses/{id}")
    suspend fun getAnalysis(@Path("id") id: Long): Response<ImageAnalysisResponse>

    @GET("/api/patient/heatmaps")
    suspend fun getAllHeatmaps(): Response<Map<String, String>>

    @GET("/api/patient/images")
    suspend fun getAllImages(): Response<Map<String, String>>

    @GET("/api/patient/analyses/pdf/{id}")
    suspend fun exportAnalysisToPdf(@Path("id") id: Long): Response<ResponseBody>
}