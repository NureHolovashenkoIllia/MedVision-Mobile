package ua.nure.holovashenko.medvision_mobile.presentation.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") }
            )
        }
    ) { padding ->
        Text(
            text = "This is the profile screen.",
            modifier = Modifier.padding(padding).padding(16.dp)
        )
    }
}
