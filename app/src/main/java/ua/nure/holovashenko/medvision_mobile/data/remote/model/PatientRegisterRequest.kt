package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class PatientRegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val birthDate: String,
    val gender: String
)