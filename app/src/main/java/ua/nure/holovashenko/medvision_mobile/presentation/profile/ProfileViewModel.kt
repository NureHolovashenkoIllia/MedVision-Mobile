package ua.nure.holovashenko.medvision_mobile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.domain.model.UserProfile
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole
import ua.nure.holovashenko.medvision_mobile.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile
    val avatar = MutableStateFlow<ByteArray?>(null)

    private var editedProfile: UserProfile? = null

    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    fun loadProfile() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            launch {
                repository.getAvatar()
                    .onSuccess {
                        avatar.value = it
                    }
                    .onFailure {
                        errorMessage.value = it.message
                    }
            }

            repository.getProfile()
                .onSuccess {
                    _userProfile.value = it
                    editedProfile = it
                }
                .onFailure {
                    errorMessage.value = it.message
                }

            isLoading.value = false
        }
    }

    fun setEditedProfile(updated: UserProfile) {
        editedProfile = updated
    }

    fun saveProfile() {
        val updated = editedProfile ?: return
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = when (updated.role) {
                UserRole.PATIENT -> repository.updatePatientProfile(updated)
                UserRole.DOCTOR -> repository.updateDoctorProfile(updated)
                else -> Result.failure(IllegalArgumentException("Unknown role"))
            }

            result.onSuccess {
                _userProfile.value = updated
            }.onFailure {
                errorMessage.value = it.message
            }

            isLoading.value = false
        }
    }

    fun uploadAvatar(bytes: ByteArray, name: String) {
        viewModelScope.launch {
            repository.uploadAvatar(bytes, name).onSuccess {
                avatar.value = bytes
            }
        }
    }
}

