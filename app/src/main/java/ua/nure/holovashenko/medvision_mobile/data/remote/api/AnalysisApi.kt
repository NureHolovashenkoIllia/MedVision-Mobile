package ua.nure.holovashenko.medvision_mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse

interface AnalysisApi {

    @GET("analysis/patient/{patientId}")
    suspend fun getAnalysesByPatient(@Path("patientId") patientId: Long): Response<List<ImageAnalysisResponse>>

    @GET("analysis/{id}")
    suspend fun getAnalysisById(@Path("id") id: Long): Response<ImageAnalysisResponse>
}