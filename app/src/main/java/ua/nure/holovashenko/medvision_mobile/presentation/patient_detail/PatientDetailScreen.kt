package ua.nure.holovashenko.medvision_mobile.presentation.patient_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.presentation.common.BreadcrumbNavigation

@Composable
fun PatientDetailScreen(
    patientId: Long,
    onBackClick: () -> Unit,
    onAddAnalysisClick: (Long) -> Unit,
    onAnalysisClick: (Long) -> Unit,
    viewModel: PatientDetailViewModel = hiltViewModel()
) {
    val patient by viewModel.patient.collectAsState()
    val analyses by viewModel.analyses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.loadData(patientId)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { onAddAnalysisClick(patientId) }) {
                        Icon(Icons.Default.Add, contentDescription = "Додати аналіз")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text("Помилка: $error", color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    BreadcrumbNavigation(
                        path = listOf(
                            "Пацієнти" to onBackClick
                        ),
                        current = "Аналізи"
                    )

                    Spacer(Modifier.height(4.dp))

                    if (patient != null) {
                        Text("Ім'я: ${patient!!.user.userName}")
                        Text("Email: ${patient!!.user.email}")
                        patient!!.birthDate?.let { bd -> Text("Дата народження: $bd") }
                        patient!!.lastExamDate?.let { le -> Text("Останній огляд: $le") }
                        Spacer(Modifier.height(16.dp))
                    }

                    Text("Аналізи", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    if (analyses.isEmpty()) {
                        Text("Немає аналізів", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        analyses.forEach { analysis ->
                            Card(Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onAnalysisClick(analysis.imageAnalysisId) }) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("ID: ${analysis.imageAnalysisId}")
                                    Text("Дата: ${analysis.creationDatetime}")
                                    Text("Статус: ${analysis.analysisStatus}")
                                    analysis.analysisDiagnosis?.let {
                                        Text("Діагноз: $it")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
