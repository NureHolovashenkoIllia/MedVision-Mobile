package ua.nure.holovashenko.medvision_mobile.data.repository

import okhttp3.ResponseBody
import ua.nure.holovashenko.medvision_mobile.data.remote.datasource.UserRemoteDataSource
import ua.nure.holovashenko.medvision_mobile.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remote: UserRemoteDataSource
) : UserRepository {
    override suspend fun getUserAvatar(userId: Long): Result<ResponseBody> = runCatching {
        val response = remote.getUserAvatar(userId)
        if (response.isSuccessful) response.body() ?: throw Exception("Порожнє тіло")
        else throw Exception("Помилка завантаження аватара: ${response.code()}")
    }
}