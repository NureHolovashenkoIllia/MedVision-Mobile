package ua.nure.holovashenko.medvision_mobile.presentation.doctor_panel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DoctorPanelScreen(
    onProfileClick: () -> Unit,
    onPatientClick: (Long) -> Unit,
    viewModel: DoctorPanelViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val patients by viewModel.filteredPatients.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPatients()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Панель лікаря") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Профіль")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                label = { Text("Пошук за іменем") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            SortDropdown(sortBy = sortBy, onSortChange = { viewModel.sortBy.value = it })

            Spacer(Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text("Помилка: $errorMessage", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn {
                    items(patients) { patient ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onPatientClick(patient.patientId) }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Ім'я: ${patient.user.userName}")
                                Text("Email: ${patient.user.email}")
                                patient.birthDate?.let { Text("Дата народження: $it") }
                                patient.lastExamDate?.let { Text("Останній огляд: $it") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortDropdown(
    sortBy: DoctorPanelViewModel.SortOption,
    onSortChange: (DoctorPanelViewModel.SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Сортування: ${sortBy.name}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DoctorPanelViewModel.SortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
