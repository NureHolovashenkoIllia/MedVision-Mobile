package ua.nure.holovashenko.medvision_mobile.domain.repository

import okhttp3.MultipartBody
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AddNoteRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse

interface DoctorRepository {
    suspend fun uploadAndAnalyzeImage(
        file: MultipartBody.Part,
        patientId: Long,
        doctorId: Long
    ): Result<String>
    suspend fun getAnalysis(id: Long): Result<ImageAnalysisResponse>
    suspend fun getHeatmap(id: Long): Result<ByteArray>
    suspend fun updateDiagnosis(id: Long, diagnosis: String): Result<Unit>
    suspend fun getAllPatients(): Result<List<PatientResponse>>
    suspend fun getPatientById(id: Long): Result<PatientResponse>
    suspend fun addNote(
        analysisId: Long,
        doctorId: Long,
        request: AddNoteRequest
    ): Result<Unit>
}
