package ua.nure.holovashenko.medvision_mobile.data.remote.datasource

import ua.nure.holovashenko.medvision_mobile.data.remote.api.UserApi
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val api: UserApi
) {
    suspend fun getUserAvatar(userId: Long) = api.getUserAvatar(userId)
}