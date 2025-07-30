package ua.nure.holovashenko.medvision_mobile.presentation.analysis_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnalysisDetailScreen(
    analysisId: Long,
    onBackClick: () -> Unit,
    viewModel: AnalysisDetailViewModel = hiltViewModel()
) {
    val analysis by viewModel.analysis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(analysisId) {
        viewModel.loadAnalysis(analysisId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Деталі аналізу") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text("Помилка: $error", color = MaterialTheme.colorScheme.error)
                analysis != null -> {
                    Text("ID: ${analysis!!.imageAnalysisId}")
                    Text("Дата: ${analysis!!.creationDatetime}")
                    Text("Статус: ${analysis!!.analysisStatus}")
                    Text("Точність (accuracy): ${analysis!!.analysisAccuracy}")
                    Text("Точність (precision): ${analysis!!.analysisPrecision}")
                    Text("Повнота (recall): ${analysis!!.analysisRecall}")
                    Text("Діагноз: ${analysis!!.analysisDiagnosis ?: "-"}")
                    Text("Деталі: ${analysis!!.analysisDetails ?: "-"}")
                    Text("Рекомендації: ${analysis!!.treatmentRecommendations ?: "-"}")
                }
            }
        }
    }
}
