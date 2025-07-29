package ua.nure.holovashenko.medvision_mobile.data.remote.datasource

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import ua.nure.holovashenko.medvision_mobile.data.remote.api.AuthApi
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AuthResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DoctorEditRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.LoginRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientEditRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientRegisterRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.UserProfileResponse
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi
) {

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = authApi.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerPatient(request: PatientRegisterRequest): Result<AuthResponse> {
        return try {
            val response = authApi.registerPatient(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<UserProfileResponse> {
        return try {
            val response = authApi.getProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePatient(request: PatientEditRequest) {
        authApi.updatePatient(request)
    }

    suspend fun updateDoctor(request: DoctorEditRequest) {
        authApi.updateDoctor(request)
    }

    suspend fun getAvatar(): Response<ResponseBody> {
        return authApi.getAvatar()
    }

    suspend fun uploadAvatar(image: MultipartBody.Part): Response<ResponseBody> {
        return authApi.uploadAvatar(image)
    }
}