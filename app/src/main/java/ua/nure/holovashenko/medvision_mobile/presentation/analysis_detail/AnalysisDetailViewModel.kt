package ua.nure.holovashenko.medvision_mobile.presentation.analysis_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.domain.repository.AnalysisRepository
import javax.inject.Inject

@HiltViewModel
class AnalysisDetailViewModel @Inject constructor(
    private val analysisRepository: AnalysisRepository
) : ViewModel() {

    private val _analysis = MutableStateFlow<ImageAnalysisResponse?>(null)
    val analysis: StateFlow<ImageAnalysisResponse?> = _analysis

    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun loadAnalysis(id: Long) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val result = analysisRepository.getAnalysisById(id)
            result.onSuccess {
                _analysis.value = it
            }.onFailure {
                error.value = it.message
            }

            isLoading.value = false
        }
    }
}
