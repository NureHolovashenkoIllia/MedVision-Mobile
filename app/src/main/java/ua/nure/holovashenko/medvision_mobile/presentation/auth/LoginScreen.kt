package ua.nure.holovashenko.medvision_mobile.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onRegisterClick: () -> Unit,
    onLoginSuccess: (UserRole) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (val result = authState) {
            is AuthResult.Success -> {
                viewModel.clearState()
                onLoginSuccess(result.role)
            }
            else -> {}
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.login(email, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onRegisterClick) {
            Text("Don’t have an account? Register")
        }
        when (authState) {
            is AuthResult.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            is AuthResult.Error -> Text((authState as AuthResult.Error).message, color = MaterialTheme.colorScheme.error)
            else -> {}
        }
    }
}
