package ua.nure.holovashenko.medvision_mobile.data.remote.datasource

import okhttp3.MultipartBody
import ua.nure.holovashenko.medvision_mobile.data.remote.api.DoctorApi
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AddNoteRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryRequest
import javax.inject.Inject

class DoctorRemoteDataSource @Inject constructor(
    private val api: DoctorApi
) {

    suspend fun uploadAndAnalyzeImage(file: MultipartBody.Part, patientId: Long, doctorId: Long) =
        api.uploadAndAnalyzeImage(file, patientId, doctorId)

    suspend fun getAnalysis(id: Long) = api.getAnalysis(id)

    suspend fun getHeatmap(id: Long) = api.getHeatmap(id)

    suspend fun getImage(id: Long) = api.getImage(id)

    suspend fun getAllPatients() = api.getAllPatients()

    suspend fun getPatientById(id: Long) = api.getPatientById(id)

    suspend fun addNote(analysisId: Long, doctorId: Long, request: AddNoteRequest) =
        api.addNote(analysisId, doctorId, request)

    suspend fun updateDiagnosis(request: DiagnosisHistoryRequest) =
        api.updateDiagnosis(request)

    suspend fun getDiagnosisHistory(analysisId: Long) =
        api.getDiagnosisHistory(analysisId)
}
