package ua.nure.holovashenko.medvision_mobile.data.repository

import ua.nure.holovashenko.medvision_mobile.data.remote.datasource.PatientRemoteDataSource
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.domain.repository.PatientRepository
import android.util.Base64
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    private val remote: PatientRemoteDataSource
) : PatientRepository {

    override suspend fun getMyAnalyses(): Result<List<ImageAnalysisResponse>> = runCatching {
        val response = remote.getMyAnalyses()
        if (response.isSuccessful) response.body()!!
        else throw Exception("Не вдалося отримати власні аналізи: ${response.code()}")
    }

    override suspend fun getUnviewedAnalyses(): Result<List<ImageAnalysisResponse>> = runCatching {
        val response = remote.getUnviewedAnalyses()
        if (response.isSuccessful) response.body()!!
        else throw Exception("Не вдалося отримати непроглянуті аналізи: ${response.code()}")
    }

    override suspend fun getAnalysis(id: Long): Result<ImageAnalysisResponse> = runCatching {
        val response = remote.getAnalysis(id)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Аналіз не знайдено: ${response.code()}")
    }

    override suspend fun getAllHeatmaps(): Result<Map<Long, ByteArray>> = runCatching {
        val response = remote.getAllHeatmaps()
        if (response.isSuccessful) {
            val body = response.body()!!
            body.toLongByteArrayMap()
        } else throw Exception("Не вдалося отримати теплові карти: ${response.code()}")
    }

    override suspend fun getHeatmap(id: Long): Result<ByteArray> =
        getAllHeatmaps().mapCatching { it[id] ?: throw Exception("Теплову карту не знайдено для аналізу $id") }

    override suspend fun getAllImages(): Result<Map<Long, ByteArray>> = runCatching {
        val response = remote.getAllImages()
        if (response.isSuccessful)
        {
            val body = response.body()!!
            body.toLongByteArrayMap()
        } else throw Exception("Не вдалося отримати зображення: ${response.code()}")
    }

    override suspend fun getImage(id: Long): Result<ByteArray> =
        getAllImages().mapCatching { it[id] ?: throw Exception("Зображення не знайдено для аналізу $id") }

    override suspend fun exportAnalysisToPdf(id: Long): Result<ByteArray> = runCatching {
        val response = remote.exportAnalysisToPdf(id)
        if (response.isSuccessful) response.body()!!.bytes()
        else throw Exception("Не вдалося експортувати аналіз у PDF: ${response.code()}")
    }

    fun Map<String, String>.toLongByteArrayMap(): Map<Long, ByteArray> =
        this.mapNotNull { (key, value) ->
            val id = key.toLongOrNull()
            val bytes = try { Base64.decode(value, Base64.DEFAULT) } catch (_: Exception) { null }
            if (id != null && bytes != null) id to bytes else null
        }.toMap()
}