package ua.nure.holovashenko.medvision_mobile.data.repository

import android.util.Log
import ua.nure.holovashenko.medvision_mobile.data.local.AuthPreferences
import ua.nure.holovashenko.medvision_mobile.data.remote.datasource.AuthRemoteDataSource
import ua.nure.holovashenko.medvision_mobile.data.remote.model.LoginRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientRegisterRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.UserProfileResponse
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult
import ua.nure.holovashenko.medvision_mobile.domain.model.UserProfile
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole
import ua.nure.holovashenko.medvision_mobile.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun login(request: LoginRequest): AuthResult {
        return remoteDataSource.login(request).fold(
            onSuccess = {
                authPreferences.saveToken(it.token)
                val role = parseRoleFromToken(it.token)
                if (role != null) {
                    AuthResult.Success(role)
                } else {
                    AuthResult.Error("Невідома роль користувача")
                }
            },
            onFailure = {
                AuthResult.Error(it.message ?: "Помилка авторизації")
            }
        )
    }

    override suspend fun registerPatient(request: PatientRegisterRequest): AuthResult {
        return remoteDataSource.registerPatient(request).fold(
            onSuccess = {
                authPreferences.saveToken(it.token)
                val role = parseRoleFromToken(it.token)
                if (role != null) {
                    AuthResult.Success(role)
                } else {
                    AuthResult.Error("Невідома роль користувача")
                }
            },
            onFailure = {
                AuthResult.Error(it.message ?: "Помилка реєстрації")
            }
        )
    }

    override suspend fun getProfile(): Result<UserProfile> {
        return remoteDataSource.getProfile().mapCatching { dto ->
            when (dto) {
                is UserProfileResponse.PatientProfileResponse -> {
                    UserProfile(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        role = UserRole.PATIENT,
                        birthDate = dto.birthDate,
                        gender = dto.gender,
                        heightCm = dto.heightCm,
                        weightKg = dto.weightKg,
                        chronicDiseases = dto.chronicDiseases,
                        allergies = dto.allergies,
                        address = dto.address,
                        lastExamDate = dto.lastExamDate
                    )
                }
                is UserProfileResponse.DoctorProfileResponse -> {
                    UserProfile(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        role = UserRole.DOCTOR,
                        position = dto.position,
                        department = dto.department,
                        licenseNumber = dto.licenseNumber,
                        education = dto.education,
                        achievements = dto.achievements,
                        medicalInstitution = dto.medicalInstitution
                    )
                }
            }
        }
    }

    private fun parseRoleFromToken(token: String): UserRole? {
        val TAG = "JwtParser"

        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.w(TAG, "Token does not have 3 parts: $token")
                return null
            }

            val payload = parts[1]
            val decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP)
            val json = String(decodedBytes, Charsets.UTF_8)

            Log.d(TAG, "Decoded JWT payload: $json")

            val roleRegex = Regex("\"role\"\\s*:\\s*\"(\\w+)\"")
            val match = roleRegex.find(json)

            if (match == null) {
                Log.w(TAG, "Role not found in payload")
                return null
            }

            val roleString = match.groups[1]?.value
            Log.d(TAG, "Extracted role string: $roleString")

            val role = UserRole.fromString(roleString)
            if (role == null) {
                Log.w(TAG, "Unknown role value: $roleString")
            }

            role
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse role from token", e)
            null
        }
    }
}