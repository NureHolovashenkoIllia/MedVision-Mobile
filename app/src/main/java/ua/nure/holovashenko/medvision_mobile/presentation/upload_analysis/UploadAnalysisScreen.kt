package ua.nure.holovashenko.medvision_mobile.presentation.upload_analysis

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.presentation.common.BreadcrumbNavigation

@Composable
fun UploadAnalysisScreen(
    patientId: Long,
    doctorId: Long,
    onAnalysisUploaded: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: UploadAnalysisViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val imageUri by viewModel.selectedImageUri
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.setImage(uri)
    }

    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                BreadcrumbNavigation(
                    path = listOf(
                        "Пацієнти" to { onBackClick(); onBackClick() },
                        "Дослідження" to onBackClick
                    ),
                    current = "Нове дослідження"
                )

                Spacer(Modifier.height(24.dp))

                Text("КТ-знімок пацієнта", style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (imageUri != null) 320.dp else 160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { galleryLauncher.launch("image/*") }
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        val bitmap = remember(imageUri) {
                            val inputStream = context.contentResolver.openInputStream(imageUri!!)
                            inputStream?.use {
                                BitmapFactory.decodeStream(it)?.asImageBitmap()
                            }
                        }
                        bitmap?.let {
                            Image(
                                bitmap = it,
                                contentDescription = "Вибране зображення",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.upload_image_ic),
                                contentDescription = "Завантажити зображення",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.height(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Натисніть, щоб обрати зображення",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        imageUri?.let { uri ->
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val imageBytes = inputStream?.use { it.readBytes() }
                            if (imageBytes != null) {
                                viewModel.uploadAnalysis(imageBytes, patientId, doctorId, onAnalysisUploaded)
                            } else {
                                Toast.makeText(context, "Неможливо прочитати файл", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = imageUri != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Завантажити та провести дослідження")
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Опрацювання зображення…", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
