package ua.nure.holovashenko.medvision_mobile.presentation.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.nure.holovashenko.medvision_mobile.domain.model.UserProfile
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole
import java.io.File
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.StateFlow
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.domain.model.Gender
import ua.nure.holovashenko.medvision_mobile.presentation.auth.GenderSegmentedButtonRow
import java.util.Calendar

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.shadow(4.dp),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(stringResource(R.string.error_message, error ?: ""), color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                profile != null -> ProfileBody(profile = profile!!, avatarFlow = viewModel.avatar, viewModel, onProfileChange = viewModel::setEditedProfile)
            }
        }
    }
}

@Composable
fun ProfileBody(
    profile: UserProfile,
    avatarFlow: StateFlow<ByteArray?>,
    viewModel: ProfileViewModel,
    onProfileChange: (UserProfile) -> Unit
) {
    val avatarState = avatarFlow.collectAsState()
    val avatar = avatarState.value
    var editableProfile by remember { mutableStateOf(profile) }
    val context = LocalContext.current
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    viewModel.uploadAvatar(bytes, "avatar.png")
                }
            }
        }
    }
    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val destUri = Uri.fromFile(File(context.cacheDir, "cropped.png"))

            val options = UCrop.Options().apply {
                setCompressionFormat(Bitmap.CompressFormat.PNG)
                setCompressionQuality(100)
            }

            val intent = UCrop.of(it, destUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(512, 512)
                .withOptions(options)
                .getIntent(context)

            cropLauncher.launch(intent)
        }
    }

    var nameError by remember { mutableStateOf<String?>(null) }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var positionError by remember { mutableStateOf<String?>(null) }
    var departmentError by remember { mutableStateOf<String?>(null) }

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
                ProfileAvatar(
                    avatar = avatar,
                    onClick = { pickLauncher.launch("image/*") }
                )

                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    ValidatedProfileField(
                        label = stringResource(R.string.name),
                        value = editableProfile.name,
                        error = nameError,
                        onValueChange = {
                            editableProfile = editableProfile.copy(name = it)
                            nameError = viewModel.validateName(it)
                            onProfileChange(editableProfile)
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editableProfile.email,
                        onValueChange = {},
                        enabled = false,
                        label = { Text(stringResource(R.string.email)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        when (editableProfile.role) {
            UserRole.PATIENT -> {
                item {
                    SectionCard(stringResource(R.string.section_medical_indicators)) {
                        GenderPickerField(
                            selected = editableProfile.gender ?: "",
                            error = genderError,
                            onSelected = {
                                editableProfile = editableProfile.copy(gender = it)
                                genderError = viewModel.validateGender(it)
                                onProfileChange(editableProfile)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        BirthDatePickerField(
                            birthDate = editableProfile.birthDate ?: "",
                            error = birthDateError,
                            onDateSelected = {
                                editableProfile = editableProfile.copy(birthDate = it)
                                birthDateError = viewModel.validateBirthDate(it)
                                onProfileChange(editableProfile)
                            }
                        )

                        ValidatedProfileField(
                            label = stringResource(R.string.height),
                            value = editableProfile.heightCm?.toString() ?: "",
                            error = heightError,
                            onValueChange = {
                                editableProfile = editableProfile.copy(heightCm = it.toDoubleOrNull())
                                heightError = viewModel.validateHeight(it)
                                onProfileChange(editableProfile)
                            },
                            keyboardType = KeyboardType.Number
                        )

                        ValidatedProfileField(
                            label = stringResource(R.string.weight),
                            value = editableProfile.weightKg?.toString() ?: "",
                            error = weightError,
                            onValueChange = {
                                editableProfile = editableProfile.copy(weightKg = it.toDoubleOrNull())
                                weightError = viewModel.validateWeight(it)
                                onProfileChange(editableProfile)
                            },
                            keyboardType = KeyboardType.Number
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    SectionCard(stringResource(R.string.section_health_status)) {
                        ProfileField(stringResource(R.string.chronic_diseases), editableProfile.chronicDiseases ?: "") {
                            editableProfile = editableProfile.copy(chronicDiseases = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField(stringResource(R.string.allergies), editableProfile.allergies ?: "") {
                            editableProfile = editableProfile.copy(allergies = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField(stringResource(R.string.address), editableProfile.address ?: "") {
                            editableProfile = editableProfile.copy(address = it)
                            onProfileChange(editableProfile)
                        }
                    }
                }
            }

            UserRole.DOCTOR -> {
                item {
                    SectionCard(stringResource(R.string.section_professional)) {
                        var position = stringResource(R.string.position)
                        ValidatedProfileField(
                            label = position,
                            value = editableProfile.position ?: "",
                            error = positionError,
                            onValueChange = {
                                editableProfile = editableProfile.copy(position = it)
                                positionError = viewModel.validateNotEmpty(it, position)
                                onProfileChange(editableProfile)
                            }
                        )
                        var department = stringResource(R.string.department)
                        ValidatedProfileField(
                            label = department,
                            value = editableProfile.department ?: "",
                            error = departmentError,
                            onValueChange = {
                                editableProfile = editableProfile.copy(department = it)
                                departmentError = viewModel.validateNotEmpty(it, department)
                                onProfileChange(editableProfile)
                            }
                        )
                        ProfileField(stringResource(R.string.medical_institution), editableProfile.medicalInstitution ?: "") {
                            editableProfile = editableProfile.copy(medicalInstitution = it)
                            onProfileChange(editableProfile)
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    SectionCard(stringResource(R.string.section_qualification)) {
                        ProfileField(stringResource(R.string.education), editableProfile.education ?: "") {
                            editableProfile = editableProfile.copy(education = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField(stringResource(R.string.achievements), editableProfile.achievements ?: "") {
                            editableProfile = editableProfile.copy(achievements = it)
                            onProfileChange(editableProfile)
                        }
                        ProfileField(stringResource(R.string.license_number), editableProfile.licenseNumber ?: "") {
                            editableProfile = editableProfile.copy(licenseNumber = it)
                            onProfileChange(editableProfile)
                        }
                    }
                }
            }

            else -> {}
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }

        item {
            var position = stringResource(R.string.position)
            var department = stringResource(R.string.department)
            Button(
                onClick = {
                    val valid = when (editableProfile.role) {
                        UserRole.PATIENT -> {
                            nameError = viewModel.validateName(editableProfile.name)
                            birthDateError = viewModel.validateBirthDate(editableProfile.birthDate ?: "")
                            genderError = viewModel.validateGender(editableProfile.gender ?: "")
                            heightError = viewModel.validateHeight(editableProfile.heightCm?.toString() ?: "")
                            weightError = viewModel.validateWeight(editableProfile.weightKg?.toString() ?: "")
                            listOf(nameError, birthDateError, genderError, heightError, weightError).all { it == null }
                        }
                        UserRole.DOCTOR -> {
                            nameError = viewModel.validateName(editableProfile.name)
                            positionError = viewModel.validateNotEmpty(editableProfile.position ?: "", position)
                            departmentError = viewModel.validateNotEmpty(editableProfile.department ?: "", department)
                            listOf(nameError, positionError, departmentError).all { it == null }
                        }
                        else -> true
                    }

                    if (valid) viewModel.saveProfile()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_changes))
            }
        }
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
fun GenderPickerField(
    selected: String,
    error: String?,
    onSelected: (String) -> Unit
) {
    val selectedGender = remember(selected) {
        Gender.entries.find { it.name == selected } ?: Gender.OTHER
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        GenderSegmentedButtonRow(
            selectedGender = selectedGender,
            onGenderSelected = { gender ->
                onSelected(gender.name)
            }
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun BirthDatePickerField(
    birthDate: String,
    error: String?,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = birthDate,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.birth_date)) },
            isError = error != null,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.select_date),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }

    if (showDatePicker) {
        DisposableEffect(Unit) {
            val datePicker = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formatted = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                    onDateSelected(formatted)
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.setOnCancelListener { showDatePicker = false }
            datePicker.show()
            onDispose {}
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

@Composable
fun ValidatedProfileField(
    label: String,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ProfileAvatar(
    avatar: ByteArray?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(avatar) {
        avatar?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .shadow(4.dp, CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.avatar),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.account_ic),
                    contentDescription = stringResource(R.string.avatar),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-5).dp, y = (-5).dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edit_ic),
                contentDescription = stringResource(R.string.change_avatar),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(18.dp)
            )
        }
    }
}