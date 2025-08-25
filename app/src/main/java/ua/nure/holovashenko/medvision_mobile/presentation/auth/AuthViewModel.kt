package ua.nure.holovashenko.medvision_mobile.presentation.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.data.remote.model.LoginRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientRegisterRequest
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult
import ua.nure.holovashenko.medvision_mobile.domain.model.Gender
import ua.nure.holovashenko.medvision_mobile.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val app: Application
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState.asStateFlow()

    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val birthDate = MutableStateFlow("")
    val gender = MutableStateFlow(Gender.OTHER)

    val passwordVisible = MutableStateFlow(false)

    fun togglePasswordVisibility() {
        passwordVisible.value = !passwordVisible.value
    }

    fun validateEmail(email: String) = when {
        email.isBlank() -> app.getString(R.string.email_required)
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> app.getString(R.string.invalid_email)
        else -> null
    }

    fun validatePassword(password: String) = when {
        password.isBlank() -> app.getString(R.string.password_required)
        password.length < 6 -> app.getString(R.string.password_too_short)
        else -> null
    }

    fun validateBirthDate(date: String) =
        if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
            app.getString(R.string.invalid_date)
        else null

    fun validateName(name: String) = if (name.isBlank()) app.getString(R.string.name_required) else null

    fun validateGender(gender: String) = if (gender.isBlank()) app.getString(R.string.gender_required) else null

    fun login() {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = authRepository.login(
                LoginRequest(email.value, password.value)
            )
        }
    }

    fun registerPatient() {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = authRepository.registerPatient(
                PatientRegisterRequest(
                    name = name.value,
                    email = email.value,
                    password = password.value,
                    birthDate = birthDate.value,
                    gender = gender.value.name
                )
            )
        }
    }

    fun clearState() {
        _authState.value = null
    }
}