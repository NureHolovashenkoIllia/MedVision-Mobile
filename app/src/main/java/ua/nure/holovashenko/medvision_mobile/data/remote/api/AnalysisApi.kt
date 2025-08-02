package ua.nure.holovashenko.medvision_mobile.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ComparisonReport
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse

interface AnalysisApi {

    @GET("analysis/patient/{patientId}")
    suspend fun getAnalysesByPatient(@Path("patientId") patientId: Long): Response<List<ImageAnalysisResponse>>

    @GET("analysis/{id}")
    suspend fun getAnalysisById(@Path("id") id: Long): Response<ImageAnalysisResponse>

    @GET("analysis/compare")
    suspend fun compareAnalyses(@Query("fromId") fromId: Long, @Query("toId") toId: Long): Response<ComparisonReport>

    @GET("analysis/compare/pdf")
    @Streaming
    suspend fun downloadComparisonPdf(@Query("fromId") fromId: Long, @Query("toId") toId: Long): Response<ResponseBody>
}