package ua.nure.holovashenko.medvision_mobile.domain.model

sealed class AuthResult {
    data class Success(val role: UserRole) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}