package ua.nure.holovashenko.medvision_mobile.presentation.patient_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ComparisonReport
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse
import ua.nure.holovashenko.medvision_mobile.domain.repository.AnalysisRepository
import ua.nure.holovashenko.medvision_mobile.domain.repository.DoctorRepository
import ua.nure.holovashenko.medvision_mobile.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class PatientDetailViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val analysisRepository: AnalysisRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _patient = MutableStateFlow<PatientResponse?>(null)
    val patient: StateFlow<PatientResponse?> = _patient.asStateFlow()

    private val _analyses = MutableStateFlow<List<ImageAnalysisResponse>>(emptyList())
    val analyses: StateFlow<List<ImageAnalysisResponse>> = _analyses.asStateFlow()

    private val _avatar = MutableStateFlow<ByteArray?>(null)
    val avatar: StateFlow<ByteArray?> = _avatar.asStateFlow()

    private val _comparisonReport = MutableStateFlow<ComparisonReport?>(null)
    val comparisonReport: StateFlow<ComparisonReport?> = _comparisonReport.asStateFlow()

    private val _selectedAnalyses = MutableStateFlow<Set<Long>>(emptySet())
    val selectedAnalyses: StateFlow<Set<Long>> = _selectedAnalyses.asStateFlow()

    private val _heatmaps = MutableStateFlow<Map<Long, ByteArray>>(emptyMap())
    val heatmaps: StateFlow<Map<Long, ByteArray>> = _heatmaps.asStateFlow()

    private val _showComparisonDialog = MutableStateFlow(false)
    val showComparisonDialog: StateFlow<Boolean> = _showComparisonDialog.asStateFlow()

    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun hideComparisonPopup() {
        _showComparisonDialog.value = false
    }

    fun clearError() {
        error.value = null
    }

    fun loadData(patientId: Long) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val patientResult = doctorRepository.getPatientById(patientId)
            val analysesResult = analysisRepository.getAnalysesByPatient(patientId)

            patientResult.onSuccess {
                _patient.value = it
                loadAvatar(it.patientId)
            }.onFailure {
                error.value = it.message
            }

            analysesResult.onSuccess { list ->
                _analyses.value = list
                list.forEach { loadHeatmap(it.imageAnalysisId) }
            }.onFailure {
                error.value = it.message
            }

            isLoading.value = false
        }
    }

    fun toggleAnalysisSelection(id: Long) {
        _selectedAnalyses.value = _selectedAnalyses.value.let {
            if (it.contains(id)) it - id else if (it.size < 2) it + id else it
        }
    }

    fun compareSelectedAnalyses() {
        val ids = _selectedAnalyses.value.toList()
        if (ids.size == 2) {
            viewModelScope.launch {
                isLoading.value = true
                analysisRepository.compareAnalyses(ids[0], ids[1]).onSuccess {
                    _comparisonReport.value = it
                    _showComparisonDialog.value = true
                }.onFailure {
                    error.value = it.message
                }
                isLoading.value = false
            }
        }
    }

    fun downloadComparisonPdf(onPdfDownloaded: (ByteArray) -> Unit) {
        val ids = _selectedAnalyses.value.toList()
        if (ids.size == 2) {
            viewModelScope.launch {
                isLoading.value = true
                analysisRepository.downloadComparisonPdf(ids[0], ids[1])
                    .onSuccess { data ->
                        onPdfDownloaded(data)
                    }.onFailure {
                    error.value = it.message
                }
                isLoading.value = false
            }
        }
    }

    private fun loadAvatar(userId: Long) {
        viewModelScope.launch {
            userRepository.getUserAvatar(userId).onSuccess { responseBody ->
                _avatar.value = responseBody.bytes()
            }
        }
    }

    private fun loadHeatmap(analysisId: Long) {
        viewModelScope.launch {
            doctorRepository.getHeatmap(analysisId).onSuccess { bytes ->
                _heatmaps.value = _heatmaps.value + (analysisId to bytes)
            }
        }
    }
}