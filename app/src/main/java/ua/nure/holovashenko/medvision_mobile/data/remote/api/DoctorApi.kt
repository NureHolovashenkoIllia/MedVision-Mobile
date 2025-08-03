package ua.nure.holovashenko.medvision_mobile.data.remote.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AddNoteRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse

interface DoctorApi {

    @Multipart
    @POST("doctor/images/analyze")
    suspend fun uploadAndAnalyzeImage(
        @Part file: MultipartBody.Part,
        @Part("patientId") patientId: Long,
        @Part("doctorId") doctorId: Long
    ): Response<ResponseBody>

    @GET("doctor/analysis/{id}")
    suspend fun getAnalysis(@Path("id") id: Long): Response<ImageAnalysisResponse>

    @GET("doctor/heatmap/{id}")
    @Streaming
    suspend fun getHeatmap(@Path("id") id: Long): Response<ResponseBody>

    @GET("doctor/image/{id}")
    @Streaming
    suspend fun getImage(@Path("id") id: Long): Response<ResponseBody>

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

    @POST("diagnosis")
    suspend fun updateDiagnosis(
        @Body request: DiagnosisHistoryRequest
    ): Response<DiagnosisHistoryResponse>

    @GET("diagnosis/analysis/{analysisId}")
    suspend fun getDiagnosisHistory(@Path("analysisId") id: Long): Response<List<DiagnosisHistoryResponse>>
}