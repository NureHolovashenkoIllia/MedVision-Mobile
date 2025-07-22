package ua.nure.holovashenko.medvision_mobile.data.remote.model

import java.math.BigDecimal

data class PatientEditRequest(
    val name: String,
    val birthDate: String,
    val gender: String,
    val heightCm: BigDecimal?,
    val weightKg: BigDecimal?,
    val chronicDiseases: String?,
    val allergies: String?,
    val address: String?
)