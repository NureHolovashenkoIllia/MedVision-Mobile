package ua.nure.holovashenko.medvision_mobile.presentation.patient_detail

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ComparisonReport
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse
import ua.nure.holovashenko.medvision_mobile.presentation.common.ActionButton
import ua.nure.holovashenko.medvision_mobile.presentation.common.BreadcrumbNavigation
import ua.nure.holovashenko.medvision_mobile.presentation.common.InfoRow
import ua.nure.holovashenko.medvision_mobile.presentation.common.Loading
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PatientDetailScreen(
    patientId: Long,
    onBackClick: () -> Unit,
    onAddAnalysisClick: (Long) -> Unit,
    onAnalysisClick: (Long) -> Unit,
    viewModel: PatientDetailViewModel = hiltViewModel()
) {
    val context: Context = LocalContext.current
    val patient by viewModel.patient.collectAsState()
    val analyses by viewModel.analyses.collectAsState()
    val avatar by viewModel.avatar.collectAsState()
    val heatmaps by viewModel.heatmaps.collectAsState()
    val showDialog by viewModel.showComparisonDialog.collectAsState()
    val report = viewModel.comparisonReport.collectAsState().value
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val analysesPerPage = 5
    var currentPage by remember { mutableIntStateOf(0) }

    val totalPages = (analyses.size + analysesPerPage - 1) / analysesPerPage
    val pagedAnalyses = analyses.drop(currentPage * analysesPerPage).take(analysesPerPage)

    val selectedCount = viewModel.selectedAnalyses.collectAsState().value.size

    LaunchedEffect(patientId) {
        viewModel.loadData(patientId)
    }

    var errorText = stringResource(R.string.error_message, error ?: "")
    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
            delay(3000)
            viewModel.clearError()
        }
    }

    if (showDialog && report != null) {
        ComparisonDialog(
            report = report,
            onDismiss = { viewModel.hideComparisonPopup() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.shadow(4.dp),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { onAddAnalysisClick(patientId) }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_analysis))
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (patient != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp)
                ) {
                    item {
                        BreadcrumbNavigation(
                            path = listOf(stringResource(R.string.patients) to onBackClick),
                            current = stringResource(R.string.analyses)
                        )
                        Spacer(Modifier.height(16.dp))
                        PatientInfoCard(patient = patient!!, avatar = avatar)
                        Spacer(Modifier.height(16.dp))
                        Text(stringResource(R.string.analyses), style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                    }

                    if (analyses.isEmpty()) {
                        item {
                            EmptyAnalysesState(onAddAnalysisClick = {
                                onAddAnalysisClick(
                                    patientId
                                )
                            })
                        }
                    } else {
                        items(pagedAnalyses) { analysis ->
                            val isSelected =
                                viewModel.selectedAnalyses.collectAsState().value.contains(
                                    analysis.imageAnalysisId
                                )
                            val heatmapBytes = heatmaps[analysis.imageAnalysisId]
                            AnalysisCard(
                                analysis = analysis,
                                isSelected = isSelected,
                                onToggleSelect = { viewModel.toggleAnalysisSelection(analysis.imageAnalysisId) },
                                onClick = { onAnalysisClick(analysis.imageAnalysisId) },
                                heatmapBytes = heatmapBytes
                            )
                        }

                        if (totalPages > 1) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = { if (currentPage > 0) currentPage-- },
                                        enabled = currentPage > 0
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                            contentDescription = stringResource(R.string.previous_page)
                                        )
                                    }

                                    Spacer(Modifier.width(16.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        repeat(totalPages) { index ->
                                            val color = if (index == currentPage)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.outlineVariant

                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(RoundedCornerShape(5.dp))
                                                    .background(color)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(16.dp))

                                    IconButton(
                                        onClick = { if (currentPage < totalPages - 1) currentPage++ },
                                        enabled = currentPage < totalPages - 1
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = stringResource(R.string.next_page)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Loading(stringResource(R.string.loading_data))
            }

            BottomActionBar(
                visible = selectedCount == 2,
                onCompareClick = { viewModel.compareSelectedAnalyses() },
                onDownloadClick = {
                    viewModel.downloadComparisonPdf { data ->
                        savePdfFile(context = context, data = data, fileName = "analysis_comparison_${System.currentTimeMillis()}.pdf")
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun PatientInfoCard(patient: PatientResponse, avatar: ByteArray?) {
    var showMedicalInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.general_info), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row {
                val avatarModifier = Modifier
                    .size(128.dp)
                    .padding(end = 16.dp)

                if (avatar != null) {
                    val bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.patient_avatar),
                        modifier = avatarModifier
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.account_ic),
                        contentDescription = stringResource(R.string.account_icon),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = avatarModifier
                    )
                }

                Column(Modifier.weight(1f)) {
                    InfoRow(stringResource(R.string.name), patient.user.userName)
                    InfoRow(stringResource(R.string.email), patient.user.email)
                    InfoRow(stringResource(R.string.birth_date), patient.birthDate ?: stringResource(R.string.unknown))
                }
            }

            InfoRow(stringResource(R.string.gender), patient.gender ?: stringResource(R.string.unknown))
            InfoRow(stringResource(R.string.last_exam), patient.lastExamDate ?: stringResource(R.string.unknown))

            if (showMedicalInfo) {
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.medical_info), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                InfoRow(stringResource(R.string.height), patient.heightCm?.toPlainString() ?: stringResource(R.string.unknown))
                InfoRow(stringResource(R.string.weight), patient.weightKg?.toPlainString() ?: stringResource(R.string.unknown))
                InfoRow(stringResource(R.string.chronic_diseases), patient.chronicDiseases ?: stringResource(R.string.none))
                InfoRow(stringResource(R.string.allergies), patient.allergies ?: stringResource(R.string.no_allergies))
                InfoRow(stringResource(R.string.address), patient.address ?: stringResource(R.string.no_address))
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = if (showMedicalInfo) stringResource(R.string.hide) else stringResource(R.string.more),
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
private fun EmptyAnalysesState(onAddAnalysisClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.medvision_ic),
            contentDescription = stringResource(R.string.no_analyses_icon),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.no_analyses_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_analyses_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddAnalysisClick) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_analysis_btn))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.add_first_analysis))
        }
    }
}

@Composable
fun AnalysisCard(
    analysis: ImageAnalysisResponse,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onClick: () -> Unit,
    heatmapBytes: ByteArray?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                heatmapBytes?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap()
                    bitmap?.let { img ->
                        Image(
                            bitmap = img,
                            contentDescription = stringResource(R.string.heatmap),
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(Modifier.width(16.dp))
                    }
                }

                Column(Modifier.weight(1f)) {
                    InfoRow(stringResource(R.string.date), formatDateTime(analysis.creationDatetime))
                    InfoRow(stringResource(R.string.status), analysis.analysisStatus.toString())
                }

                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() }
                )
            }

            analysis.analysisDiagnosis?.let {
                Spacer(Modifier.height(8.dp))
                InfoRow(stringResource(R.string.diagnosis), it)
            }
        }
    }
}

@Composable
fun BottomActionBar(
    visible: Boolean,
    onCompareClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                ActionButton(
                    text = stringResource(R.string.compare),
                    painter = painterResource(id = R.drawable.compare_ic),
                    onClick = onCompareClick,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                ActionButton(
                    text = stringResource(R.string.download),
                    painter = painterResource(id = R.drawable.download_ic),
                    onClick = onDownloadClick,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun ComparisonDialog(
    report: ComparisonReport,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) { Text(stringResource(R.string.close)) }
        },
        title = { Text(stringResource(R.string.comparison_title)) },
        text = {
            Column {
                InfoRow(stringResource(R.string.date1), formatDateTime(report.createdAtFrom))
                InfoRow(stringResource(R.string.date2), formatDateTime(report.createdAtTo))

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        InfoRow(stringResource(R.string.ct_image1), "")
                        ComparisonImage(base64 = report.fromImageBase64)
                    }
                    Column(Modifier.weight(1f)) {
                        InfoRow(stringResource(R.string.ct_image2), "")
                        ComparisonImage(base64 = report.toImageBase64)
                    }
                    Column(Modifier.weight(1f)) {
                        InfoRow(stringResource(R.string.difference), "")
                        ComparisonImage(base64 = report.diffHeatmap)
                    }
                }

                Spacer(Modifier.height(8.dp))

                InfoRow(
                    stringResource(R.string.diagnosis),
                    "${firstWord(report.diagnosisTextFrom.toString())} -> ${firstWord(report.diagnosisTextTo.toString())}"
                )
                InfoRow(
                    stringResource(R.string.accuracy),
                    "${formatDouble(report.accuracyFrom?.toDouble())} -> ${formatDouble(report.accuracyTo?.toDouble())}"
                )
                InfoRow(
                    stringResource(R.string.recall),
                    "${formatDouble(report.recallFrom?.toDouble())} -> ${formatDouble(report.recallTo?.toDouble())}"
                )
                InfoRow(
                    stringResource(R.string.precision),
                    "${formatDouble(report.precisionFrom?.toDouble())} -> ${formatDouble(report.precisionTo?.toDouble())}"
                )
            }
        }
    )
}

@Composable
fun ComparisonImage(base64: String) {
    val decodedBytes = remember(base64) {
        android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
    }
    val bitmap = remember(decodedBytes) {
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

fun formatDateTime(isoString: String): String {
    return try {
        val parsed = LocalDateTime.parse(isoString)
        parsed.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    } catch (_: Exception) {
        isoString
    }
}

fun firstWord(text: String): String = text.substringBefore(" ")

fun formatDouble(value: Double?): String = value?.let { String.format("%.3f", it) } ?: "-"

fun savePdfFile(context: Context, data: ByteArray, fileName: String) {
    val outputStream: OutputStream

    try {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        outputStream = FileOutputStream(file)
        outputStream.write(data)
        outputStream.close()

        Toast.makeText(context, context.getString(R.string.file_saved, file.name), Toast.LENGTH_LONG).show()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)

    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.file_save_error, e.message), Toast.LENGTH_LONG).show()
    }
}