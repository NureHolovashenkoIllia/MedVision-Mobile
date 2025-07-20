package ua.nure.holovashenko.medvision_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ua.nure.holovashenko.medvision_mobile.presentation.navigation.NavigationGraph
import ua.nure.holovashenko.medvision_mobile.presentation.ui.theme.MedVisionMobileTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedVisionMobileTheme {
                NavigationGraph()
            }
        }
    }
}