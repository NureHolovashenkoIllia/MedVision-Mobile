package ua.nure.holovashenko.medvision_mobile.presentation.doctor_panel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse
import ua.nure.holovashenko.medvision_mobile.domain.repository.DoctorRepository
import javax.inject.Inject

@HiltViewModel
class DoctorPanelViewModel @Inject constructor(
    private val repository: DoctorRepository
) : ViewModel() {

    private val _allPatients = MutableStateFlow<List<PatientResponse>>(emptyList())
    val allPatients: StateFlow<List<PatientResponse>> = _allPatients.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val sortBy = MutableStateFlow(SortOption.NAME)

    val filteredPatients: StateFlow<List<PatientResponse>> = combine(
        _allPatients, searchQuery, sortBy
    ) { patients, query, sort ->
        patients
            .filter {
                it.user.userName.contains(query, ignoreCase = true)
            }
            .sortedWith(
                when (sort) {
                    SortOption.NAME -> compareBy { it.user.userName }
                    SortOption.AGE -> compareBy { it.birthDate }
                    SortOption.LAST_EXAM -> compareByDescending { it.lastExamDate }
                }
            )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    fun loadPatients() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.getAllPatients()
            result.onSuccess {
                _allPatients.value = it
            }.onFailure {
                errorMessage.value = it.message
                Log.w("doctor", "error: ${it.message}")
            }

            isLoading.value = false
        }
    }

    enum class SortOption { NAME, AGE, LAST_EXAM }
}
