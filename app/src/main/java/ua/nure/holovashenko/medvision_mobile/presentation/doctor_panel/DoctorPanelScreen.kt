package ua.nure.holovashenko.medvision_mobile.presentation.doctor_panel

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.domain.model.SortOption
import ua.nure.holovashenko.medvision_mobile.data.remote.model.PatientResponse

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
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.shadow(4.dp),
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchAndSortBar(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.searchQuery.value = it },
                    sortBy = sortBy,
                    onSortChange = { viewModel.sortBy.value = it }
                )
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Text(stringResource(R.string.error_message, errorMessage ?: ""), color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(patients) { patient ->
                        PatientCard(patient = patient, onClick = {
                            onPatientClick(patient.patientId)
                        })
                    }

                    item {
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAndSortBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    sortBy: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    val sortOptions = SortOption.entries
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(stringResource(R.string.search_patients)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search)
                )
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { menuExpanded = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.sort_ic),
                    contentDescription = stringResource(R.string.sort),
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                sortOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(option.labelRes),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = if (option == sortBy) FontWeight.Bold else FontWeight.Normal
                                )
                                if (option == sortBy) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = stringResource(R.string.chosen),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSortChange(option)
                            menuExpanded = false
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun PatientCard(
    patient: PatientResponse,
    onClick: () -> Unit,
    viewModel: DoctorPanelViewModel = hiltViewModel()
) {
    val avatarBitmap by viewModel.avatars.collectAsState()

    LaunchedEffect(patient.user.userId) {
        viewModel.loadAvatar(patient.user.userId)
    }

    val bitmap = avatarBitmap[patient.user.userId]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.avatar),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.no_avatar),
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = patient.user.userName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = patient.user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            patient.birthDate?.let {
                Text(
                    text = stringResource(R.string.birth_date, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = stringResource(R.string.last_exam_date, patient.lastExamDate ?: "-"),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}