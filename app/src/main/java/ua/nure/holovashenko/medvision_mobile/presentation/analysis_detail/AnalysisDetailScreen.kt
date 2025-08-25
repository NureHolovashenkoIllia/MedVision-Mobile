package ua.nure.holovashenko.medvision_mobile.presentation.analysis_detail

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AddNoteRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.AnalysisNoteResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryRequest
import ua.nure.holovashenko.medvision_mobile.data.remote.model.DiagnosisHistoryResponse
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.domain.model.AnalysisStatus
import ua.nure.holovashenko.medvision_mobile.presentation.common.BreadcrumbNavigation
import ua.nure.holovashenko.medvision_mobile.presentation.common.InfoRow
import ua.nure.holovashenko.medvision_mobile.presentation.common.Loading
import ua.nure.holovashenko.medvision_mobile.presentation.patient_detail.formatDateTime
import kotlin.math.abs
import kotlin.math.min

@Composable
fun AnalysisDetailScreen(
    analysisId: Long,
    doctorId: Long,
    onBackClick: () -> Unit,
    viewModel: AnalysisDetailViewModel = hiltViewModel()
) {
    val analysis by viewModel.analysis.collectAsState()
    val notes by viewModel.notes.collectAsState()
    var selectedNoteId by remember { mutableStateOf<Long?>(null) }
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                                stringResource(R.string.patients) to { onBackClick(); onBackClick() },
                                stringResource(R.string.analyses) to onBackClick
                            ),
                            current = stringResource(R.string.analysis_details)
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
                                stringResource(R.string.doctor_notes),
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(Modifier.height(8.dp))

                            NotesImage(
                                imageBytes = imageBytes!!,
                                notes = notes,
                                selectedNoteId = selectedNoteId,
                                onAddNote = { request ->
                                    viewModel.addNote(
                                        analysis!!.imageAnalysisId,
                                        doctorId,
                                        request
                                    )
                                }
                            )

                            Spacer(Modifier.height(8.dp))
                            notes.forEach { note ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedNoteId =
                                                if (selectedNoteId == note.analysisNoteId) null else note.analysisNoteId
                                        }
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = cardColors(
                                        containerColor = if (selectedNoteId == note.analysisNoteId)
                                            MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    NoteCard(note)
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    item {
                        Text(stringResource(R.string.diagnosis_history), style = MaterialTheme.typography.headlineSmall)
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
                Loading(stringResource(R.string.loading_data))
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
            Text(stringResource(R.string.main_info), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row {
                val avatarModifier = Modifier
                    .size(128.dp)
                    .padding(end = 16.dp)

                if (heatmapBytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(heatmapBytes, 0, heatmapBytes.size)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.heatmap),
                        modifier = avatarModifier
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.image_ic),
                        contentDescription = stringResource(R.string.account_icon),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = avatarModifier
                    )
                }

                Column(Modifier.weight(1f)) {
                    InfoRow(stringResource(R.string.date), analysis.creationDatetime.toString())
                    InfoRow(stringResource(R.string.diagnosis), analysis.analysisDiagnosis ?: "-")
                }
            }

            InfoRow(stringResource(R.string.recommendations), analysis.treatmentRecommendations ?: "-")

            if (showMedicalInfo) {
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.additional_info), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                InfoRow(stringResource(R.string.details), analysis.analysisDetails ?: "-")
                InfoRow(stringResource(R.string.accuracy), analysis.analysisAccuracy?.toString() ?: "-")
                InfoRow(stringResource(R.string.recall), analysis.analysisRecall?.toString() ?: "-")
                InfoRow(stringResource(R.string.precision), analysis.analysisPrecision?.toString() ?: "-")
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
fun StatusSelector(
    currentStatus: AnalysisStatus,
    onStatusChange: (AnalysisStatus) -> Unit
) {
    val options = AnalysisStatus.entries.toTypedArray()
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(R.string.diagnosis_status),
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
                    contentDescription = if (expanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
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
                    InfoRow(stringResource(R.string.doctor), item.doctorName.toString())
                    InfoRow(stringResource(R.string.diagnosis), item.diagnosisText)
                }

                Text(
                    text = formatDateTime(item.timestamp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (hasAdditionalInfo) {

                if (showAdditionalInfo) {
                    item.reason.takeIf { it.isNotBlank() }?.let {
                        InfoRow(stringResource(R.string.reason), it)
                    }

                    item.analysisDetails.takeIf { !it.isNullOrBlank() }?.let {
                        InfoRow(stringResource(R.string.details), it)
                    }

                    item.treatmentRecommendations.takeIf { !it.isNullOrBlank() }?.let {
                        InfoRow(stringResource(R.string.recommendations), it)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (showAdditionalInfo) stringResource(R.string.hide) else stringResource(R.string.more),
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
fun NotesImage(
    imageBytes: ByteArray,
    notes: List<AnalysisNoteResponse>,
    selectedNoteId: Long?,
    onAddNote: (AddNoteRequest) -> Unit
) {
    val density = LocalDensity.current
    var startX by remember { mutableStateOf<Float?>(null) }
    var startY by remember { mutableStateOf<Float?>(null) }
    var endX by remember { mutableStateOf<Float?>(null) }
    var endY by remember { mutableStateOf<Float?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }

    val imageBitmap = imageBytes.toImageBitmap()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        startX = offset.x
                        startY = offset.y
                        endX = offset.x
                        endY = offset.y
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        endX = (endX ?: startX!!) + dragAmount.x
                        endY = (endY ?: startY!!) + dragAmount.y
                    },
                    onDragEnd = {
                        if (startX != null && startY != null && endX != null && endY != null) {
                            showDialog = true
                        }
                    }
                )
            }
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = stringResource(R.string.ct_image_with_notes),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val displayedNotes = selectedNoteId?.let { id ->
            notes.filter { it.analysisNoteId == id }
        } ?: emptyList()

        displayedNotes.forEach { note ->
            Box(
                modifier = Modifier
                    .offset(
                        with(density) { note.noteAreaX.toDp() },
                        with(density) { note.noteAreaY.toDp() }
                    )
                    .size(
                        with(density) { note.noteAreaWidth.toDp() },
                        with(density) { note.noteAreaHeight.toDp() }
                    )
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
            )
        }

        if (startX != null && startY != null && endX != null && endY != null && !showDialog) {
            val left = min(startX!!, endX!!)
            val top = min(startY!!, endY!!)
            val width = abs(endX!! - startX!!)
            val height = abs(endY!! - startY!!)

            Box(
                modifier = Modifier
                    .offset(with(density) { left.toDp() }, with(density) { top.toDp() })
                    .size(with(density) { width.toDp() }, with(density) { height.toDp() })
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
        }
    }

    fun resetSelection() {
        startX = null
        startY = null
        endX = null
        endY = null
    }

    if (showDialog) {
        AddNoteDialog(
            noteText = noteText,
            onNoteTextChange = { noteText = it },
            onDismiss = {
                resetSelection()
                noteText = ""
                showDialog = false
            },
            onSave = {
                if (noteText.isNotBlank() && startX != null && startY != null && endX != null && endY != null) {
                    onAddNote(
                        AddNoteRequest(
                            noteText = noteText,
                            noteAreaX = startX?.toInt(),
                            noteAreaY = startY?.toInt(),
                            noteAreaWidth = (endX!! - startX!!).toInt(),
                            noteAreaHeight = (endY!! - startY!!).toInt()
                        )
                    )
                    resetSelection()
                    noteText = ""
                    showDialog = false
                }
            }
        )
    }
}

@Composable
private fun AddNoteDialog(
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                Text(stringResource(R.string.add_note), style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = noteText,
                    onValueChange = onNoteTextChange,
                    label = { Text(stringResource(R.string.note_text)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onSave) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(note: AnalysisNoteResponse) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.note_number, note.analysisNoteId),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = formatDateTime(note.creationDatetime),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = note.noteText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

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
            Text(stringResource(R.string.add_diagnosis))
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
                        text = stringResource(R.string.update_diagnosis),
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = diagnosisText,
                        onValueChange = { diagnosisText = it },
                        label = { Text(stringResource(R.string.diagnosis)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text(stringResource(R.string.reason)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text(stringResource(R.string.details)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = recommendations,
                        onValueChange = { recommendations = it },
                        label = { Text(stringResource(R.string.recommendations)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.cancel))
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
                            Text(stringResource(R.string.save))
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