package ua.nure.holovashenko.medvision_mobile.domain.repository

import ua.nure.holovashenko.medvision_mobile.data.remote.model.LoginRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientRegisterRequest
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult
import ua.nure.holovashenko.medvision_mobile.domain.model.UserProfile

interface AuthRepository {
    suspend fun login(request: LoginRequest): AuthResult
    suspend fun registerPatient(request: PatientRegisterRequest): AuthResult
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updatePatientProfile(profile: UserProfile): Result<Unit>
    suspend fun updateDoctorProfile(profile: UserProfile): Result<Unit>
    suspend fun getAvatar(): Result<ByteArray>
    suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<String>
}