package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class UserProfileResponse(
    val role: String,
    val id: Long,
    val name: String,
    val email: String,
    val birthDate: String?,
    val gender: String?,
    val heightCm: Double?,
    val weightKg: Double?,
    val chronicDiseases: String?,
    val allergies: String?,
    val address: String?,
    val lastExamDate: String?,
    val position: String?,
    val department: String?,
    val licenseNumber: String?,
    val education: String?,
    val achievements: String?,
    val medicalInstitution: String?
)