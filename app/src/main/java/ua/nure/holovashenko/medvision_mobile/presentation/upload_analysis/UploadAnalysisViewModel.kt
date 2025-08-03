package ua.nure.holovashenko.medvision_mobile.presentation.upload_analysis

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ua.nure.holovashenko.medvision_mobile.domain.repository.DoctorRepository
import javax.inject.Inject

@HiltViewModel
class UploadAnalysisViewModel @Inject constructor(
    private val repository: DoctorRepository
) : ViewModel() {

    var selectedImageUri = mutableStateOf<Uri?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var error = mutableStateOf<String?>(null)
        private set

    fun setImage(uri: Uri?) {
        selectedImageUri.value = uri
    }

    fun uploadAnalysis(imageBytes: ByteArray, patientId: Long, doctorId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)

            repository.uploadAndAnalyzeImage(part, patientId, doctorId)
                .onSuccess { onSuccess() }
                .onFailure {
                    error.value = it.message
                }
            isLoading.value = false
        }
    }

    fun clearError() {
        error.value = null
    }
}