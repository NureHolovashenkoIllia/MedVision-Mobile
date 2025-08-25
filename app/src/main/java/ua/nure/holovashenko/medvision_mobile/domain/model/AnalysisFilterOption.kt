package ua.nure.holovashenko.medvision_mobile.domain.model

import androidx.annotation.StringRes
import ua.nure.holovashenko.medvision_mobile.R

enum class AnalysisFilterOption(@StringRes val labelRes: Int) {
    ALL(R.string.filter_all),
    VIEWED_ONLY(R.string.filter_viewed),
    UNVIEWED_ONLY(R.string.filter_unviewed)
}