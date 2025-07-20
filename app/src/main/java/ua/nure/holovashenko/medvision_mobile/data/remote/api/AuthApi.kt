package ua.nure.holovashenko.medvision_mobile.data.remote.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AuthResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.LoginRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientRegisterRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.UserProfileResponse

interface AuthApi {

    @POST("auth/register/patient")
    suspend fun registerPatient(@Body request: PatientRegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("auth/profile")
    suspend fun getProfile(): UserProfileResponse
}