package ua.nure.holovashenko.medvision_mobile.domain.model

enum class UserRole {
    PATIENT,
    DOCTOR;

    companion object {
        fun fromString(value: String?): UserRole? {
            return when (value?.uppercase()) {
                "PATIENT" -> PATIENT
                "DOCTOR" -> DOCTOR
                else -> null
            }
        }
    }
}
