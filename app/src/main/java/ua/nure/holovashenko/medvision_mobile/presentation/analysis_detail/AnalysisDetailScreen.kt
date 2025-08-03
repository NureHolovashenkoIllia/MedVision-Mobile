package ua.nure.holovashenko.medvision_mobile.presentation.analysis_detail

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.domain.model.AnalysisStatus
import ua.nure.holovashenko.medvision_mobile.presentation.common.BreadcrumbNavigation
import ua.nure.holovashenko.medvision_mobile.presentation.common.InfoRow
import ua.nure.holovashenko.medvision_mobile.presentation.common.Loading
import ua.nure.holovashenko.medvision_mobile.presentation.patient_detail.formatDateTime

@Composable
fun AnalysisDetailScreen(
    analysisId: Long,
    doctorId: Long,
    onBackClick: () -> Unit,
    viewModel: AnalysisDetailViewModel = hiltViewModel()
) {
    val analysis by viewModel.analysis.collectAsState()
    val imageBytes by viewModel.imageBytes.collectAsState()
    val heatmapBytes by viewModel.heatmapBytes.collectAsState()
    val diagnosisHistory by viewModel.diagnosisHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(analysisId) {
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
                modifier = Modifier.shadow(4.dp),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                if (analysis != null) {
                    item {
                        BreadcrumbNavigation(
                            path = listOf(
                                "Пацієнти" to { onBackClick(); onBackClick() },
                                "Дослідження" to onBackClick
                            ),
                            current = "Деталі дослідження"
                        )
                        Spacer(Modifier.height(16.dp))

                        AnalysisInfoCard(analysis!!, heatmapBytes)

                        Spacer(Modifier.height(16.dp))

                        StatusSelector(
                            currentStatus = analysis!!.analysisStatus,
                            onStatusChange = {
                                viewModel.updateStatus(
                                    analysis!!.imageAnalysisId,
                                    it
                                )
                            }
                        )

                        Spacer(Modifier.height(16.dp))
                    }

                    if (imageBytes != null) {
                        item {
                            Text(
                                "Нотатки лікарів",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(Modifier.height(8.dp))

                            Image(
                                bitmap = imageBytes!!.toImageBitmap(),
                                contentDescription = "Original Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .shadow(2.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    item {
                        Text("Історія діагнозів", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                    }

                    items(diagnosisHistory) {
                        DiagnosisHistoryCard(it)
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        ToggleableUpdateDiagnosisSection(
                            analysisId = analysis!!.imageAnalysisId,
                            doctorId = doctorId,
                            onUpdate = { viewModel.updateDiagnosis(it) }
                        )
                    }
                }
            }

            if (isLoading) {
                Loading("Завантаження даних...")
            }
        }
    }
}

@Composable
fun AnalysisInfoCard(analysis: ImageAnalysisResponse, heatmapBytes: ByteArray?) {
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
                        contentDescription = "Іконка акаунту",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = avatarModifier
                    )
                }

                Column(Modifier.weight(1f)) {
                    InfoRow("Дата", analysis.creationDatetime.toString())
                    InfoRow("Діагноз", analysis.analysisDiagnosis ?: "-")
                }
            }

            InfoRow("Рекомендації", analysis.treatmentRecommendations ?: "-")

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
fun StatusSelector(
    currentStatus: AnalysisStatus,
    onStatusChange: (AnalysisStatus) -> Unit
) {
    val options = AnalysisStatus.entries.toTypedArray()
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Статус діагнозу",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentStatus.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Згорнути" else "Розгорнути"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = status.name,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = if (status == currentStatus) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        expanded = false
                        onStatusChange(status)
                    }
                )
            }
        }
    }
}

@Composable
private fun DiagnosisHistoryCard(item: DiagnosisHistoryResponse) {
    var showAdditionalInfo by remember { mutableStateOf(false) }
    val hasAdditionalInfo = item.reason.isNotBlank()
            || !item.analysisDetails.isNullOrBlank()
            || !item.treatmentRecommendations.isNullOrBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Column(Modifier.weight(1f)) {
                    InfoRow("Лікар", item.doctorName.toString())
                    InfoRow("Діагноз", item.diagnosisText)
                }

                Text(
                    text = formatDateTime(item.timestamp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (hasAdditionalInfo) {

                if (showAdditionalInfo) {
                    item.reason.takeIf { it.isNotBlank() }?.let {
                        InfoRow("Причина", it)
                    }

                    item.analysisDetails.takeIf { !it.isNullOrBlank() }?.let {
                        InfoRow("Деталі", it)
                    }

                    item.treatmentRecommendations.takeIf { !it.isNullOrBlank() }?.let {
                        InfoRow("Рекомендації", it)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (showAdditionalInfo) "Сховати" else "Докладніше",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { showAdditionalInfo = !showAdditionalInfo }
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ToggleableUpdateDiagnosisSection(
    analysisId: Long,
    doctorId: Long,
    onUpdate: (DiagnosisHistoryRequest) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var diagnosisText by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var recommendations by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Додати діагноз")
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Оновити діагноз",
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = diagnosisText,
                        onValueChange = { diagnosisText = it },
                        label = { Text("Діагноз") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Причина") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text("Деталі") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = recommendations,
                        onValueChange = { recommendations = it },
                        label = { Text("Рекомендації") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Скасувати")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (diagnosisText.isNotBlank()) {
                                    onUpdate(
                                        DiagnosisHistoryRequest(
                                            analysisId = analysisId,
                                            diagnosisText = diagnosisText,
                                            doctorId = doctorId,
                                            reason = reason,
                                            analysisDetails = details,
                                            treatmentRecommendations = recommendations
                                        )
                                    )
                                    diagnosisText = ""
                                    reason = ""
                                    details = ""
                                    recommendations = ""
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Зберегти")
                        }
                    }
                }
            }
        }
    }
}

fun ByteArray.toImageBitmap(): ImageBitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size).asImageBitmap()
}