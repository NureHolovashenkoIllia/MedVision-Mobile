package ua.nure.holovashenko.medvision_mobile.presentation.profile

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.nure.holovashenko.medvision_mobile.domain.model.UserProfile
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val avatar by viewModel.avatar.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мій профіль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Вийти")
                    }
                }
            )
        },
        bottomBar = {
            if (profile != null) {
                Button(
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Зберегти зміни")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text("Помилка: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                profile != null -> ProfileBody(profile = profile!!, avatar = avatar, onProfileChange = viewModel::setEditedProfile)
            }
        }
    }
}

@Composable
fun ProfileBody(
    profile: UserProfile,
    avatar: ByteArray?,
    onProfileChange: (UserProfile) -> Unit
) {
    var editableProfile by remember { mutableStateOf(profile) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                avatar?.let {
                    val bmp = remember(avatar) {
                        try {
                            BitmapFactory.decodeByteArray(it, 0, it.size)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    bmp?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = editableProfile.name,
                        onValueChange = {
                            editableProfile = editableProfile.copy(name = it)
                            onProfileChange(editableProfile)
                        },
                        label = { Text("Ім’я") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editableProfile.email,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        when (editableProfile.role) {
            UserRole.PATIENT -> {
                item {
                    SectionCard("Медичні показники") {
                        ProfileField("Дата народження", editableProfile.birthDate ?: "") {
                            editableProfile = editableProfile.copy(birthDate = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Стать", editableProfile.gender ?: "") {
                            editableProfile = editableProfile.copy(gender = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Зріст (см)", editableProfile.heightCm?.toString() ?: "") {
                            editableProfile = editableProfile.copy(heightCm = it.toDoubleOrNull())
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Вага (кг)", editableProfile.weightKg?.toString() ?: "") {
                            editableProfile = editableProfile.copy(weightKg = it.toDoubleOrNull())
                            onProfileChange(editableProfile)
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    SectionCard("Стан здоров’я") {
                        ProfileField("Хронічні захворювання", editableProfile.chronicDiseases ?: "") {
                            editableProfile = editableProfile.copy(chronicDiseases = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Алергії", editableProfile.allergies ?: "") {
                            editableProfile = editableProfile.copy(allergies = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Адреса", editableProfile.address ?: "") {
                            editableProfile = editableProfile.copy(address = it)
                            onProfileChange(editableProfile)
                        }
                    }
                }
            }

            UserRole.DOCTOR -> {
                item {
                    SectionCard("Професійна діяльність") {
                        ProfileField("Посада", editableProfile.position ?: "") {
                            editableProfile = editableProfile.copy(position = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Відділення", editableProfile.department ?: "") {
                            editableProfile = editableProfile.copy(department = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Медичний заклад", editableProfile.medicalInstitution ?: "") {
                            editableProfile = editableProfile.copy(medicalInstitution = it)
                            onProfileChange(editableProfile)
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    SectionCard("Кваліфікація") {
                        ProfileField("Освіта", editableProfile.education ?: "") {
                            editableProfile = editableProfile.copy(education = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Досягнення", editableProfile.achievements ?: "") {
                            editableProfile = editableProfile.copy(achievements = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField("Номер ліцензії", editableProfile.licenseNumber ?: "") {
                            editableProfile = editableProfile.copy(licenseNumber = it)
                            onProfileChange(editableProfile)
                        }
                    }
                }
            }

            else -> {}
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true
    )
}