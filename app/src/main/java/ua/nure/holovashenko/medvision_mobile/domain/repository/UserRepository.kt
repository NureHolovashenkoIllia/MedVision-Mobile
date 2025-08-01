package ua.nure.holovashenko.medvision_mobile.domain.repository

import okhttp3.ResponseBody

interface UserRepository {
    suspend fun getUserAvatar(userId: Long): Result<ResponseBody>
}
