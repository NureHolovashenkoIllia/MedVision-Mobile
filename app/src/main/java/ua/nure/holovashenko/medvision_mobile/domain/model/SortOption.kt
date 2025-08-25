package ua.nure.holovashenko.medvision_mobile.domain.model

import androidx.annotation.StringRes
import ua.nure.holovashenko.medvision_mobile.R

enum class SortOption(@StringRes val labelRes: Int) {
    NAME(R.string.sort_name),
    AGE(R.string.sort_age),
    LAST_EXAM(R.string.sort_last_exam)
}