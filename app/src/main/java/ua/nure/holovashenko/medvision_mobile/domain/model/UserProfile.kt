package ua.nure.holovashenko.medvision_mobile.domain.model

data class UserProfile(
    val id: Long,
    val name: String,
    val email: String,
    val role: UserRole,

    // only for patient
    val birthDate: String? = null,
    val gender: String? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val chronicDiseases: String? = null,
    val allergies: String? = null,
    val address: String? = null,
    val lastExamDate: String? = null,

    // only for doctor
    val position: String? = null,
    val department: String? = null,
    val licenseNumber: String? = null,
    val education: String? = null,
    val achievements: String? = null,
    val medicalInstitution: String? = null
)
