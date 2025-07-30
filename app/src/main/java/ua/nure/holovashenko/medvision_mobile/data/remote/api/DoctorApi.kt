package ua.nure.holovashenko.medvision_mobile.data.remote.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AddNoteRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse

interface DoctorApi {

    @Multipart
    @POST("doctor/images/analyze")
    suspend fun uploadAndAnalyzeImage(
        @Part file: MultipartBody.Part,
        @Part("patientId") patientId: Long,
        @Part("doctorId") doctorId: Long
    ): Response<String>

    @GET("doctor/analysis/{id}")
    suspend fun getAnalysis(@Path("id") id: Long): Response<ImageAnalysisResponse>

    @GET("doctor/heatmap/{id}")
    @Streaming
    suspend fun getHeatmap(@Path("id") id: Long): Response<okhttp3.ResponseBody>

    @POST("doctor/analysis/{id}/diagnosis")
    suspend fun updateDiagnosis(
        @Path("id") id: Long,
        @Body diagnosis: String
    ): Response<Unit>

    @GET("doctor/patients")
    suspend fun getAllPatients(): Response<List<PatientResponse>>

    @GET("doctor/patients/{id}")
    suspend fun getPatientById(@Path("id") id: Long): Response<PatientResponse>

    @POST("doctor/analyses/{analysesId}/notes")
    suspend fun addNote(
        @Path("analysesId") analysisId: Long,
        @Query("doctorId") doctorId: Long,
        @Body noteRequest: AddNoteRequest
    ): Response<Unit>
}