package ua.nure.holovashenko.medvision_mobile.data.remote.datasource

import ua.nure.holovashenko.medvision_mobile.data.remote.api.AnalysisApi
import javax.inject.Inject

class AnalysisRemoteDataSource @Inject constructor(
    private val api: AnalysisApi
) {
    suspend fun getAnalysesByPatient(patientId: Long) =
        api.getAnalysesByPatient(patientId)

    suspend fun getAnalysisById(id: Long) =
        api.getAnalysisById(id)

    suspend fun compareAnalyses(fromId: Long, toId: Long) =
        api.compareAnalyses(fromId, toId)

    suspend fun downloadComparisonPdf(fromId: Long, toId: Long) =
        api.downloadComparisonPdf(fromId, toId)
}