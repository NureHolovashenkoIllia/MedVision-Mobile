package ua.nure.holovashenko.medvision_mobile.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.remote.model.LoginRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientRegisterRequest
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult
import ua.nure.holovashenko.medvision_mobile.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = authRepository.login(LoginRequest(email, password))
        }
    }

    fun registerPatient(
        name: String,
        email: String,
        password: String,
        birthDate: String,
        gender: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = authRepository.registerPatient(
                PatientRegisterRequest(name, email, password, birthDate, gender)
            )
        }
    }

    fun clearState() {
        _authState.value = null
    }
}
