package ua.nure.holovashenko.medvision_mobile.data.repository

import okhttp3.MultipartBody
import ua.nure.holovashenko.medvision_mobile.data.remote.datasource.DoctorRemoteDataSource
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AddNoteRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AnalysisNoteResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse
import ua.nure.holovashenko.medvision_mobile.domain.repository.DoctorRepository
import javax.inject.Inject

class DoctorRepositoryImpl @Inject constructor(
    private val remote: DoctorRemoteDataSource
) : DoctorRepository {

    override suspend fun uploadAndAnalyzeImage(file: MultipartBody.Part, patientId: Long, doctorId: Long): Result<String> {
        return runCatching {
            val response = remote.uploadAndAnalyzeImage(file, patientId, doctorId)
            if (response.isSuccessful) {
                response.body()?.string() ?: throw Exception("Empty response body")
            } else {
                throw Exception("Upload failed: ${response.code()}")
            }
        }
    }

    override suspend fun getAnalysis(id: Long): Result<ImageAnalysisResponse> = runCatching {
        val response = remote.getAnalysis(id)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Analysis not found")
    }

    override suspend fun getHeatmap(id: Long): Result<ByteArray> = runCatching {
        val response = remote.getHeatmap(id)
        if (response.isSuccessful) response.body()!!.bytes()
        else throw Exception("Heatmap not found")
    }

    override suspend fun getImage(id: Long): Result<ByteArray> = runCatching {
        val response = remote.getImage(id)
        if (response.isSuccessful) response.body()!!.bytes()
        else throw Exception("Image not found")
    }

    override suspend fun getAllPatients(): Result<List<PatientResponse>> = runCatching {
        val response = remote.getAllPatients()
        if (response.isSuccessful) response.body()!!
        else throw Exception("Failed to fetch patients")
    }

    override suspend fun getPatientById(id: Long): Result<PatientResponse> = runCatching {
        val response = remote.getPatientById(id)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Patient not found")
    }

    override suspend fun addNote(analysisId: Long, doctorId: Long, request: AddNoteRequest): Result<Unit> = runCatching {
        val response = remote.addNote(analysisId, doctorId, request)
        if (!response.isSuccessful) throw Exception("Failed to add note")
    }

    override suspend fun updateDiagnosis(
        request: DiagnosisHistoryRequest
    ): Result<DiagnosisHistoryResponse> = runCatching {
        val response = remote.updateDiagnosis(request)
        if (!response.isSuccessful) throw Exception("Update failed")
        response.body()!!
    }

    override suspend fun getDiagnosisHistory(analysisId: Long): Result<List<DiagnosisHistoryResponse>> =
        runCatching {
            val response = remote.getDiagnosisHistory(analysisId)
            if (response.isSuccessful) response.body()!!
            else throw Exception("Failed to fetch diagnosis history")
        }

    override suspend fun getAnalysisNotes(analysisId: Long): Result<List<AnalysisNoteResponse>> =
        runCatching {
            val response = remote.getAnalysisNotes(analysisId)
            if (response.isSuccessful) response.body()!!
            else throw Exception("Failed to fetch analysis notes")
        }
}