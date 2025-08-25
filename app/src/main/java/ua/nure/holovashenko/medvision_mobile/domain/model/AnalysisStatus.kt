package ua.nure.holovashenko.medvision_mobile.domain.model

import androidx.annotation.StringRes
import ua.nure.holovashenko.medvision_mobile.R

enum class AnalysisStatus(@StringRes val labelRes: Int) {
    PENDING(R.string.status_pending),
    REVIEWED(R.string.status_reviewed),
    REQUIRES_REVISION(R.string.status_requires_revision)
}