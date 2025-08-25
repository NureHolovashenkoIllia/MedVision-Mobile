package ua.nure.holovashenko.medvision_mobile.presentation.splash

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import ua.nure.holovashenko.medvision_mobile.R
import ua.nure.holovashenko.medvision_mobile.data.local.AuthPreferences
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole
import ua.nure.holovashenko.medvision_mobile.presentation.common.Loading
import ua.nure.holovashenko.medvision_mobile.util.parseRoleFromToken

private const val TAG = "SplashScreen"

@Composable
fun SplashScreen(
    authPreferences: AuthPreferences,
    onNavigateToLogin: () -> Unit,
    onNavigateToDoctorPanel: () -> Unit,
    onNavigateToPatientPanel: () -> Unit
) {
    LaunchedEffect(Unit) {
        val token = authPreferences.getToken()
        Log.d(TAG, "Token from SharedPreferences: $token")

        if (token.isNullOrBlank()) {
            Log.d(TAG, "Token is null or blank, navigating to Login")
            onNavigateToLogin()
            return@LaunchedEffect
        }

        val (role, userId) = parseRoleFromToken(token)
        Log.d(TAG, "Parsed role: $role, userId: $userId")

        when (role) {
            UserRole.PATIENT -> {
                onNavigateToPatientPanel()
            }

            UserRole.DOCTOR -> {
                if (userId != null) {
                    authPreferences.saveDoctorId(userId)
                    onNavigateToDoctorPanel()
                } else {
                    onNavigateToLogin()
                }
            }

            else -> {
                Log.d(TAG, "Unknown role, navigating to Login")
                onNavigateToLogin()
            }
        }
    }

    Loading(label = stringResource(R.string.loading))
}