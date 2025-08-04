package ua.nure.holovashenko.medvision_mobile.presentation.patient_panel

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.data.remote.model.ImageAnalysisResponse
import ua.nure.holovashenko.medvision_mobile.domain.model.AnalysisFilterOption
import ua.nure.holovashenko.medvision_mobile.domain.model.AnalysisSortOption
import ua.nure.holovashenko.medvision_mobile.presentation.common.InfoRow
import ua.nure.holovashenko.medvision_mobile.presentation.common.Loading
import ua.nure.holovashenko.medvision_mobile.presentation.patient_detail.formatDateTime

@Composable
fun PatientPanelScreen(
    onProfileClick: () -> Unit,
    viewModel: PatientPanelViewModel = hiltViewModel()
) {
    val analyses by viewModel.filteredAnalyses.collectAsState()
    val heatmaps by viewModel.heatmaps.collectAsState()
    val unreadAnalyses by viewModel.unreadAnalyses.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val filterOption by viewModel.filterOption.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            delay(3000)
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAnalyses()
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
                    Box {
                        IconButton(onClick = { showDropdown = !showDropdown }) {
                            BadgedBox(
                                badge = {
                                    if (unreadAnalyses.isNotEmpty()) {
                                        Badge { Text(unreadAnalyses.size.toString()) }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Unread Analyses"
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false },
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ) {
                            if (unreadAnalyses.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text(
                                        text = "No unread analyses",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Normal
                                    ) },
                                    onClick = {}
                                )
                            } else {
                                unreadAnalyses.forEach {
                                    DropdownMenuItem(
                                        text = { Text(
                                            text = "Аналіз №${it.imageAnalysisId}",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Normal
                                        ) },
                                        onClick = {
                                            showDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
                SearchAndSortBar(
                    searchQuery = searchQuery,
                    onSearchChange = viewModel::onSearchQueryChange,
                    filterOption = filterOption,
                    onFilterChange = viewModel::onFilterOptionChange,
                    sortOption = sortOption,
                    onSortChange = viewModel::onSortOptionChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(analyses) { analysis ->
                        val heatmapBytes = heatmaps[analysis.imageAnalysisId]
                        AnalysisCard(
                            analysis = analysis,
                            onClick = {  },
                            heatmapBytes = heatmapBytes
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
fun SearchAndSortBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    filterOption: AnalysisFilterOption,
    onFilterChange: (AnalysisFilterOption) -> Unit,
    sortOption: AnalysisSortOption,
    onSortChange: (AnalysisSortOption) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

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
            placeholder = { Text("Пошук аналізів") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Пошук"
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
                    .clickable { showSortMenu = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.sort_ic),
                    contentDescription = "Сортувати",
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                AnalysisSortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = option.displayName,
                                    color = if (option == sortOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (option == sortOption) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        onClick = {
                            onSortChange(option)
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showFilterMenu = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.filter_ic),
                    contentDescription = "Фільтрація",
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                AnalysisFilterOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = option.displayName,
                                    color = if (option == filterOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (option == filterOption) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        onClick = {
                            onFilterChange(option)
                            showFilterMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnalysisCard(
    analysis: ImageAnalysisResponse,
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
                            contentDescription = "Теплова карта",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(Modifier.width(16.dp))
                    }
                }

                Column(Modifier.weight(1f)) {
                    InfoRow("Дата", formatDateTime(analysis.creationDatetime))
                    InfoRow("Статус", analysis.analysisStatus.toString())
                }

                if (!analysis.viewed) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Unread",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            analysis.analysisDiagnosis?.let {
                Spacer(Modifier.height(8.dp))
                InfoRow("Діагноз", it)
            }
        }
    }
}