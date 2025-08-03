package ua.nure.holovashenko.medvision_mobile.data.remote.model

import ua.nure.holovashenko.medvision_mobile.domain.model.AnalysisStatus

data class UpdateStatusRequest(
    val status: AnalysisStatus
)