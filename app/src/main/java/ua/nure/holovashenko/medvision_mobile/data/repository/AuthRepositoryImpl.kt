package ua.nure.holovashenko.medvision_mobile.data.repository

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ua.nure.holovashenko.medvision_mobile.data.local.AuthPreferences
import ua.nure.holovashenko.medvision_mobile.data.remote.datasource.AuthRemoteDataSource
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DoctorEditRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.LoginRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientEditRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientRegisterRequest
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult
import ua.nure.holovashenko.medvision_mobile.domain.model.UserProfile
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole
import ua.nure.holovashenko.medvision_mobile.domain.repository.AuthRepository
import ua.nure.holovashenko.medvision_mobile.util.parseRoleFromToken
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun login(request: LoginRequest): AuthResult {
        return remoteDataSource.login(request).fold(
            onSuccess = {
                authPreferences.saveToken(it.token)
                val (role, userId) = parseRoleFromToken(it.token)
                if (role == UserRole.DOCTOR && userId != null) {
                    authPreferences.saveDoctorId(userId)
                }

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
                val (role, _) = parseRoleFromToken(it.token)
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
            val role = UserRole.fromString(dto.role)
            when (role) {
                UserRole.PATIENT -> UserProfile(
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
                UserRole.DOCTOR -> UserProfile(
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
                else -> throw IllegalArgumentException("Unknown role in profile")
            }
        }
    }

    override suspend fun updatePatientProfile(profile: UserProfile): Result<Unit> {
        return runCatching {
            remoteDataSource.updatePatient(
                PatientEditRequest(
                    name = profile.name,
                    birthDate = profile.birthDate!!,
                    gender = profile.gender!!,
                    heightCm = profile.heightCm?.toBigDecimal(),
                    weightKg = profile.weightKg?.toBigDecimal(),
                    chronicDiseases = profile.chronicDiseases,
                    allergies = profile.allergies,
                    address = profile.address
                )
            )
        }
    }

    override suspend fun updateDoctorProfile(profile: UserProfile): Result<Unit> {
        return runCatching {
            remoteDataSource.updateDoctor(
                DoctorEditRequest(
                    name = profile.name,
                    position = profile.position!!,
                    department = profile.department!!,
                    licenseNumber = profile.licenseNumber!!,
                    education = profile.education!!,
                    achievements = profile.achievements!!,
                    medicalInstitution = profile.medicalInstitution!!
                )
            )
        }
    }

    override suspend fun getAvatar(): Result<ByteArray> = runCatching {
        val response = remoteDataSource.getAvatar()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val bytes = body.bytes()
            Log.d("Avatar", "Avatar loaded: ${bytes.size} bytes")
            bytes
        } else {
            throw Exception("Failed to load avatar: ${response.code()}")
        }
    }

    override suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<String> =
        runCatching {
            val req = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", fileName, req)
            val response = remoteDataSource.uploadAvatar(part)

            if (response.isSuccessful) {
                response.body()?.string() ?: throw IllegalStateException("Порожня відповідь від сервера")
            } else {
                throw IllegalStateException("Помилка ${response.code()}: ${response.errorBody()?.string()}")
            }
        }
}