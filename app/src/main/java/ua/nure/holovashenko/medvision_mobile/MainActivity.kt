package ua.nure.holovashenko.medvision_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ua.nure.holovashenko.medvision_mobile.data.local.AuthPreferences
import ua.nure.holovashenko.medvision_mobile.presentation.navigation.NavigationGraph
import ua.nure.holovashenko.medvision_mobile.presentation.ui.theme.MedVisionMobileTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedVisionMobileTheme {
                NavigationGraph(authPreferences = authPreferences)
            }
        }
    }
}