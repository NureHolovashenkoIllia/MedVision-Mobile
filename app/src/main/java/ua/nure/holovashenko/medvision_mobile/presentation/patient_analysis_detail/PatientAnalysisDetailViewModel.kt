package ua.nure.holovashenko.medvision_mobile.presentation.patient_analysis_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.domain.repository.PatientRepository import javax.inject.Inject

@HiltViewModel
class PatientAnalysisDetailViewModel @Inject constructor(
    private val repository: PatientRepository
) : ViewModel() {

    private val _analysis = MutableStateFlow<ImageAnalysisResponse?>(null)
    val analysis: StateFlow<ImageAnalysisResponse?> = _analysis

    private val _heatmap = MutableStateFlow<ByteArray?>(null)
    val heatmap: StateFlow<ByteArray?> = _heatmap

    private val _image = MutableStateFlow<ByteArray?>(null)
    val image: StateFlow<ByteArray?> = _image

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAnalysis(analysisId: Long) {
        viewModelScope.launch {
            _loading.value = true
            repository.getAnalysis(analysisId)
                .onSuccess { _analysis.value = it }
                .onFailure { _error.value = it.message }
            repository.getHeatmap(analysisId)
                .onSuccess { _heatmap.value = it }
                .onFailure { _error.value = it.message }
            repository.getImage(analysisId)
                .onSuccess { _image.value = it }
                .onFailure { _error.value = it.message }
            _loading.value = false
        }
    }

    fun downloadPdf(analysisId: Long, onPdfDownloaded: (ByteArray, Long) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            repository.exportAnalysisToPdf(analysisId)
                .onSuccess { data ->
                    onPdfDownloaded(data, analysisId)
                }
                .onFailure {
                    _error.value = it.message
                }
            _loading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
