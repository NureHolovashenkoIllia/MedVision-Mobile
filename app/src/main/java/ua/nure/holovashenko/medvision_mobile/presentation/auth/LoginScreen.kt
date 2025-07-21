package ua.nure.holovashenko.medvision_mobile.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole
import ua.nure.holovashenko.medvision_mobile.R

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onRegisterClick: () -> Unit,
    onLoginSuccess: (UserRole) -> Unit
) {

    val email by viewModel.email.collectAsState()
    var emailError by remember { mutableStateOf<String?>(null) }
    var emailTouched by remember { mutableStateOf(false) }
    var emailChanged by remember { mutableStateOf(false) }

    val password by viewModel.password.collectAsState()
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordTouched by remember { mutableStateOf(false) }
    var passwordChanged by remember { mutableStateOf(false) }
    val passwordVisible by viewModel.passwordVisible.collectAsState()

    val authState by viewModel.authState.collectAsState()

    val isFormValid by remember(email, password, emailError, passwordError) {
        derivedStateOf {
            viewModel.validateEmail(email) == null && viewModel.validatePassword(password) == null
        }
    }

    LaunchedEffect(authState) {
        when (val result = authState) {
            is AuthResult.Success -> {
                viewModel.clearState()
                onLoginSuccess(result.role)
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                viewModel.email.value = it
                emailChanged = true
            },
            label = { Text("Email") },
            isError = emailTouched && emailError != null,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.mail_ic),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!it.isFocused && emailChanged) {
                        emailTouched = true
                        emailError = viewModel.validateEmail(email)
                    }
                }
        )
        if (emailTouched && emailError != null) {
            Text(
                emailError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                viewModel.password.value = it
                passwordChanged = true
            },
            label = { Text("Password") },
            isError = passwordTouched && passwordError != null,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password_ic),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!it.isFocused && passwordChanged) {
                        passwordTouched = true
                        passwordError = viewModel.validatePassword(password)
                    }
                },
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.visible else R.drawable.invisible),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
        )
        if (passwordTouched && passwordError != null) {
            Text(
                passwordError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedButton(
            onClick = {
                emailTouched = true
                passwordTouched = true
                emailError = viewModel.validateEmail(email)
                passwordError = viewModel.validatePassword(password)

                if (emailError == null && passwordError == null) {
                    viewModel.login()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid && authState !is AuthResult.Loading
        ) {
            if (authState is AuthResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onRegisterClick) {
            Text("Don’t have an account? Register")
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