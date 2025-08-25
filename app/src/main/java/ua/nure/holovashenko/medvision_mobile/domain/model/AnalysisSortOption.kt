package ua.nure.holovashenko.medvision_mobile.domain.model

import androidx.annotation.StringRes
import ua.nure.holovashenko.medvision_mobile.R

enum class AnalysisSortOption(@StringRes val labelRes: Int) {
    DATE_DESC(R.string.sort_date_desc),
    DATE_ASC(R.string.sort_date_asc),
    ACCURACY_DESC(R.string.sort_accuracy)
}