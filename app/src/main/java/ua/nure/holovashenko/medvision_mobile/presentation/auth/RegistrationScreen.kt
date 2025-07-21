package ua.nure.holovashenko.medvision_mobile.presentation.auth

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult
import ua.nure.holovashenko.medvision_mobile.domain.model.Gender
import java.util.Calendar

@Composable
fun RegistrationScreen(
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val birthDate by viewModel.birthDate.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var nameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var birthDateTouched by remember { mutableStateOf(false) }
    var genderTouched by remember { mutableStateOf(false) }

    var nameChanged by remember { mutableStateOf(false) }
    var emailChanged by remember { mutableStateOf(false) }
    var passwordChanged by remember { mutableStateOf(false) }
    var birthDateChanged by remember { mutableStateOf(false) }
    var genderChanged by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }

    val isFormValid by remember(name, email, password, birthDate, gender) {
        derivedStateOf {
            viewModel.validateName(name) == null &&
                    viewModel.validateEmail(email) == null &&
                    viewModel.validatePassword(password) == null &&
                    viewModel.validateBirthDate(birthDate) == null &&
                    viewModel.validateGender(gender.name) == null
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthResult.Success) {
            viewModel.clearState()
            onRegisterSuccess()
        }
    }

    if (showDatePicker) {
        DisposableEffect(Unit) {
            val datePicker = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formatted = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                    viewModel.birthDate.value = formatted
                    birthDateChanged = true
                    birthDateTouched = false
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.setOnCancelListener { showDatePicker = false }
            datePicker.show()
            onDispose { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Gender
        GenderSegmentedButtonRow(
            selectedGender = gender,
            onGenderSelected = {
                viewModel.gender.value = it
                genderChanged = true
                genderTouched = false // очищення при виборі
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (genderTouched && genderError != null) {
            Text(genderError!!, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = {
                viewModel.name.value = it
                nameChanged = true
            },
            label = { Text("Name") },
            isError = nameTouched && nameError != null,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!it.isFocused && nameChanged) {
                        nameTouched = true
                        nameError = viewModel.validateName(name)
                    }
                }
        )
        if (nameTouched && nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                viewModel.email.value = it
                emailChanged = true
            },
            label = { Text("Email") },
            isError = emailTouched && emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!it.isFocused && emailChanged) {
                        emailTouched = true
                        emailError = viewModel.validateEmail(email)
                    }
                }
        )
        if (emailTouched && emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.height(8.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                viewModel.password.value = it
                passwordChanged = true
            },
            label = { Text("Password") },
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.visible else R.drawable.invisible),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passwordTouched && passwordError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!it.isFocused && passwordChanged) {
                        passwordTouched = true
                        passwordError = viewModel.validatePassword(password)
                    }
                }
        )
        if (passwordTouched && passwordError != null) Text(passwordError!!, color = MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.height(8.dp))

        // Birth Date
        OutlinedTextField(
            value = birthDate,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDatePicker = true
                },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            },
            isError = birthDateTouched && birthDateError != null,
            label = { Text("Birth Date") }
        )
        if (birthDateTouched && birthDateError != null) {
            Text(birthDateError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedButton(
            onClick = {
                nameTouched = true
                emailTouched = true
                passwordTouched = true
                birthDateTouched = true
                genderTouched = true

                nameError = viewModel.validateName(name)
                emailError = viewModel.validateEmail(email)
                passwordError = viewModel.validatePassword(password)
                birthDateError = viewModel.validateBirthDate(birthDate)
                genderError = viewModel.validateGender(gender.name)

                if (isFormValid) {
                    viewModel.registerPatient()
                }
            },
            enabled = isFormValid && authState !is AuthResult.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (authState is AuthResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onLoginClick) {
            Text("Already have an account? Login")
        }

        if (authState is AuthResult.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthResult.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun GenderSegmentedButtonRow(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    val options = Gender.entries

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { gender ->
            val isSelected = gender == selectedGender
            AssistChip(
                onClick = { onGenderSelected(gender) },
                label = {
                    Text(
                        when (gender) {
                            Gender.MALE -> "Male"
                            Gender.FEMALE -> "Female"
                            Gender.OTHER -> "Other"
                        }
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}