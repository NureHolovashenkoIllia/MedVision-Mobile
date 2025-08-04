package ua.nure.holovashenko.medvision_mobile.presentation.patient_analysis_detail

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.presentation.common.ActionButton
import ua.nure.holovashenko.medvision_mobile.presentation.common.InfoRow
import ua.nure.holovashenko.medvision_mobile.presentation.common.Loading
import ua.nure.holovashenko.medvision_mobile.presentation.patient_detail.formatDateTime
import ua.nure.holovashenko.medvision_mobile.presentation.patient_detail.savePdfFile

@Composable
fun PatientAnalysisDetailScreen(
    analysisId: Long,
    onBackClick: () -> Unit,
    viewModel: PatientAnalysisDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val analysis by viewModel.analysis.collectAsState()
    val heatmap by viewModel.heatmap.collectAsState()
    val image by viewModel.image.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAnalysis(analysisId)
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
        ) {
            analysis?.let { data ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    item {
                        AnalysisInfoCard(data, heatmap, image)
                    }
                }
            }

            if (loading) {
                Loading("Завантаження даних...")
            }

            analysis?.let {
                BottomActionBar(
                    onDownloadClick = {
                        viewModel.downloadPdf(analysis!!.imageAnalysisId) { data, analysisId ->
                            savePdfFile(
                                context = context,
                                data = data,
                                fileName = "analysis_${analysisId}.pdf"
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun AnalysisInfoCard(analysis: ImageAnalysisResponse, heatmapBytes: ByteArray?, imageBytes: ByteArray?) {
    var showMedicalInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Основна інформація", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row {
                val avatarModifier = Modifier
                    .size(128.dp)
                    .padding(end = 16.dp)

                if (heatmapBytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(heatmapBytes, 0, heatmapBytes.size)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Теплова карта",
                        modifier = avatarModifier
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.image_ic),
                        contentDescription = "No heatmap",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = avatarModifier
                    )
                }

                Column(Modifier.weight(1f)) {
                    InfoRow("Дата", formatDateTime(analysis.creationDatetime.toString()))
                    InfoRow("Діагноз", analysis.analysisDiagnosis ?: "-")
                }
            }

            InfoRow("Рекомендації", analysis.treatmentRecommendations ?: "-")

            Spacer(Modifier.height(4.dp))

            Text(
                text = "КТ-знімок",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            if (imageBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "КТ-знімок",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.image_ic),
                    contentDescription = "No image",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(Modifier.height(4.dp))

            if (showMedicalInfo) {
                Spacer(Modifier.height(8.dp))
                Text("Додаткова інформація", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                InfoRow("Деталі", analysis.analysisDetails ?: "-")
                InfoRow("Точність", analysis.analysisAccuracy?.toString() ?: "-")
                InfoRow("Повнота", analysis.analysisRecall?.toString() ?: "-")
                InfoRow("Precision", analysis.analysisPrecision?.toString() ?: "-")
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = if (showMedicalInfo) "Сховати" else "Докладніше",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .clickable { showMedicalInfo = !showMedicalInfo }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun BottomActionBar(
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            ActionButton(
                text = "Завантажити",
                painter = painterResource(id = R.drawable.download_ic),
                onClick = onDownloadClick,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}