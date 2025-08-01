package ua.nure.holovashenko.medvision_mobile.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("users/{id}/avatar")
    suspend fun getUserAvatar(@Path("id") userId: Long): Response<ResponseBody>
}