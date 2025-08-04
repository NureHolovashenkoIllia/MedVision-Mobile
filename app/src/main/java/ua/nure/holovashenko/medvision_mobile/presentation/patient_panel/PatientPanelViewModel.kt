package ua.nure.holovashenko.medvision_mobile.presentation.patient_panel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.domain.model.AnalysisFilterOption
import ua.nure.holovashenko.medvision_mobile.domain.model.AnalysisSortOption
import ua.nure.holovashenko.medvision_mobile.domain.repository.PatientRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PatientPanelViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {

    private val _analyses = MutableStateFlow<List<ImageAnalysisResponse>>(emptyList())
    val analyses: StateFlow<List<ImageAnalysisResponse>> = _analyses.asStateFlow()

    private val _heatmaps = MutableStateFlow<Map<Long, ByteArray>>(emptyMap())
    val heatmaps: StateFlow<Map<Long, ByteArray>> = _heatmaps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(AnalysisSortOption.DATE_DESC)
    val sortOption: StateFlow<AnalysisSortOption> = _sortOption.asStateFlow()

    private val _filterOption = MutableStateFlow(AnalysisFilterOption.ALL)
    val filterOption: StateFlow<AnalysisFilterOption> = _filterOption.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val unreadAnalyses: StateFlow<List<ImageAnalysisResponse>> = _analyses
        .map { list -> list.filter { !it.viewed } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredAnalyses: StateFlow<List<ImageAnalysisResponse>> = combine(
        _analyses, _searchQuery, _sortOption, _filterOption
    ) { all, query, sort, filter ->

        val filtered = all.filter { analysis ->
            val searchIn = listOfNotNull(
                analysis.analysisDiagnosis,
                analysis.analysisDetails,
                analysis.treatmentRecommendations,
                analysis.doctor.userName
            ).joinToString(" ")

            searchIn.contains(query, ignoreCase = true)
        }.filter {
            when (filter) {
                AnalysisFilterOption.ALL -> true
                AnalysisFilterOption.VIEWED_ONLY -> it.viewed
                AnalysisFilterOption.UNVIEWED_ONLY -> !it.viewed
            }
        }

        when (sort) {
            AnalysisSortOption.DATE_DESC -> filtered.sortedByDescending { it.toLocalDateTime() }
            AnalysisSortOption.DATE_ASC -> filtered.sortedBy { it.toLocalDateTime() }
            AnalysisSortOption.ACCURACY_DESC -> filtered.sortedByDescending { it.analysisAccuracy ?: 0f }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortOptionChange(option: AnalysisSortOption) {
        _sortOption.value = option
    }

    fun onFilterOptionChange(option: AnalysisFilterOption) {
        _filterOption.value = option
    }

    fun loadAnalyses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = patientRepository.getMyAnalyses()

            result.onSuccess {list ->
                _analyses.value = list
                loadAllHeatmaps()
            }.onFailure {
                _analyses.value = emptyList()
                _errorMessage.value = it.message ?: "Невідома помилка при завантаженні аналізів"
            }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun loadAllHeatmaps() {
        viewModelScope.launch {
            patientRepository.getAllHeatmaps().onSuccess { map ->
                _heatmaps.value = map
            }.onFailure {
                _errorMessage.value = "Не вдалося завантажити теплові карти: ${it.message}"
                Log.e("heatmaps loading", it.message.toString())
            }
        }
    }

    private fun ImageAnalysisResponse.toLocalDateTime(): LocalDateTime {
        return try {
            LocalDateTime.parse(this.creationDatetime, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.MIN
        }
    }
}