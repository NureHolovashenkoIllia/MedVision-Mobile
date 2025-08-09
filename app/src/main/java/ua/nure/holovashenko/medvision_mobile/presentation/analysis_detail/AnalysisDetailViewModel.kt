package ua.nure.holovashenko.medvision_mobile.presentation.analysis_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AddNoteRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AnalysisNoteResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.UpdateStatusRequest
import ua.nure.holovashenko.medvision_mobile.domain.model.AnalysisStatus
import ua.nure.holovashenko.medvision_mobile.domain.repository.AnalysisRepository
import ua.nure.holovashenko.medvision_mobile.domain.repository.DoctorRepository
import javax.inject.Inject

@HiltViewModel
class AnalysisDetailViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val analysisRepository: AnalysisRepository
) : ViewModel() {

    private val _analysis = MutableStateFlow<ImageAnalysisResponse?>(null)
    val analysis: StateFlow<ImageAnalysisResponse?> = _analysis

    val notes = MutableStateFlow<List<AnalysisNoteResponse>>(emptyList())

    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    val imageBytes = MutableStateFlow<ByteArray?>(null)
    val heatmapBytes = MutableStateFlow<ByteArray?>(null)

    val diagnosisHistory = MutableStateFlow<List<DiagnosisHistoryResponse>>(emptyList())

    fun loadAnalysis(id: Long) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val analysisResult = doctorRepository.getAnalysis(id)
            analysisResult.onSuccess { analysis ->
                _analysis.value = analysis

                doctorRepository.getImage(id).onSuccess {
                    imageBytes.value = it
                }

                doctorRepository.getHeatmap(id).onSuccess {
                    heatmapBytes.value = it
                }

                doctorRepository.getDiagnosisHistory(id).onSuccess {
                    diagnosisHistory.value = it
                }

                doctorRepository.getAnalysisNotes(id).onSuccess {
                    notes.value = it
                }
            }.onFailure {
                error.value = it.message
            }

            isLoading.value = false
        }
    }

    fun updateStatus(id: Long, status: AnalysisStatus) {
        viewModelScope.launch {
            isLoading.value = true

            analysisRepository.updateAnalysisStatus(id, UpdateStatusRequest(status))
                .onSuccess { loadAnalysis(id) }
                .onFailure { error.value = it.message }

            isLoading.value = false
        }
    }

    fun updateDiagnosis(request: DiagnosisHistoryRequest) {
        viewModelScope.launch {
            isLoading.value = true

            doctorRepository.updateDiagnosis(request)
                .onSuccess { loadAnalysis(request.analysisId) }
                .onFailure { error.value = it.message }

            isLoading.value = false
        }
    }

    fun addNote(analysisId: Long, doctorId: Long, request: AddNoteRequest) {
        viewModelScope.launch {
            doctorRepository.addNote(analysisId, doctorId, request).onSuccess {
                loadNotes(analysisId)
            }.onFailure {
                error.value = it.message
            }
        }
    }

    fun loadNotes(analysisId: Long) {
        viewModelScope.launch {
            doctorRepository.getAnalysisNotes(analysisId)
                .onSuccess { notes.value = it }
                .onFailure { error.value = it.message }
        }
    }

    fun clearError() {
        error.value = null
    }
}