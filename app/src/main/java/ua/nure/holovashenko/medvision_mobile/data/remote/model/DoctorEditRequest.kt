package ua.nure.holovashenko.medvision_mobile.data.remote.model

data class DoctorEditRequest(
    val name: String,
    val position: String,
    val department: String,
    val licenseNumber: String,
    val education: String,
    val achievements: String,
    val medicalInstitution: String
)
