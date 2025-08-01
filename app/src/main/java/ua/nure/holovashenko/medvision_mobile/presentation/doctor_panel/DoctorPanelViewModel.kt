package ua.nure.holovashenko.medvision_mobile.presentation.doctor_panel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse
import ua.nure.holovashenko.medvision_mobile.domain.model.SortOption
import ua.nure.holovashenko.medvision_mobile.domain.repository.DoctorRepository
import ua.nure.holovashenko.medvision_mobile.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class DoctorPanelViewModel @Inject constructor(
    private val repository: DoctorRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _allPatients = MutableStateFlow<List<PatientResponse>>(emptyList())

    val avatars: MutableStateFlow<Map<Long, Bitmap?>> = MutableStateFlow(emptyMap())

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

    fun loadAvatar(userId: Long) {
        viewModelScope.launch {
            if (avatars.value.containsKey(userId)) return@launch
            val result = userRepository.getUserAvatar(userId)
            result.onSuccess { body ->
                val byteArray = withContext(Dispatchers.IO) { body.bytes() }
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                avatars.update { it + (userId to bitmap) }
            }.onFailure {
                Log.e("avatar", "Не вдалося отримати аватар: ${it.message}")
                avatars.update { it + (userId to null) }
            }
        }
    }
}