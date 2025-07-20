package ua.nure.holovashenko.medvision_mobile.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.nure.holovashenko.medvision_mobile.domain.model.AuthResult

@Composable
fun RegistrationScreen(
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.Success -> {
                viewModel.clearState()
                onRegisterSuccess()
            }
            else -> {}
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = birthDate, onValueChange = { birthDate = it }, label = { Text("Birth Date (YYYY-MM-DD)") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.registerPatient(name, email, password, birthDate, gender)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onLoginClick) {
            Text("Already have an account? Login")
        }
        when (authState) {
            is AuthResult.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            is AuthResult.Error -> Text((authState as AuthResult.Error).message, color = MaterialTheme.colorScheme.error)
            else -> {}
        }
    }
}