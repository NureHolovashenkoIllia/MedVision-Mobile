package ua.nure.holovashenko.medvision_mobile.presentation.patient_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse
import ua.nure.holovashenko.medvision_mobile.domain.repository.AnalysisRepository
import ua.nure.holovashenko.medvision_mobile.domain.repository.DoctorRepository
import javax.inject.Inject

@HiltViewModel
class PatientDetailViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val analysisRepository: AnalysisRepository
) : ViewModel() {

    private val _patient = MutableStateFlow<PatientResponse?>(null)
    val patient: StateFlow<PatientResponse?> = _patient.asStateFlow()

    private val _analyses = MutableStateFlow<List<ImageAnalysisResponse>>(emptyList())
    val analyses: StateFlow<List<ImageAnalysisResponse>> = _analyses.asStateFlow()

    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun loadData(patientId: Long) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val patientResult = doctorRepository.getPatientById(patientId)
            val analysesResult = analysisRepository.getAnalysesByPatient(patientId)

            patientResult.onSuccess {
                _patient.value = it
            }.onFailure {
                error.value = it.message
            }

            analysesResult.onSuccess {
                _analyses.value = it
            }.onFailure {
                error.value = it.message
            }

            isLoading.value = false
        }
    }
}
