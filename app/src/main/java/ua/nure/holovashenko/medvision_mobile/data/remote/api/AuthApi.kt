package ua.nure.holovashenko.medvision_mobile.data.remote.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AuthResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DoctorEditRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.LoginRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientEditRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientRegisterRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.UserProfileResponse

interface AuthApi {

    @POST("auth/register/patient")
    suspend fun registerPatient(@Body request: PatientRegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("auth/profile")
    suspend fun getProfile(): UserProfileResponse

    @PUT("auth/edit/patient")
    suspend fun updatePatient(@Body request: PatientEditRequest): Unit

    @PUT("auth/edit/doctor")
    suspend fun updateDoctor(@Body request: DoctorEditRequest): Unit

    @GET("auth/avatar")
    suspend fun getAvatar(): Response<ResponseBody>

    @Multipart
    @POST("auth/avatar")
    suspend fun uploadAvatar(@Part image: MultipartBody.Part): Response<String>
}