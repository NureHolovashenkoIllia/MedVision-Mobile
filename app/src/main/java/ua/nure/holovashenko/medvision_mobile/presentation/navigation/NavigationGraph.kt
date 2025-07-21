package ua.nure.holovashenko.medvision_mobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole
import ua.nure.holovashenko.medvision_mobile.presentation.auth.LoginScreen
import ua.nure.holovashenko.medvision_mobile.presentation.auth.RegistrationScreen
import ua.nure.holovashenko.medvision_mobile.presentation.doctor_panel.DoctorPanelScreen
import ua.nure.holovashenko.medvision_mobile.presentation.patient_panel.PatientPanelScreen
import ua.nure.holovashenko.medvision_mobile.presentation.profile.ProfileScreen

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Auth.route) {

        composable(Screen.Auth.route) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Screen.Registration.route)
                },
                onLoginSuccess = { role ->
                    when (role) {
                        UserRole.PATIENT -> navController.navigate(Screen.PatientPanel.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                        UserRole.DOCTOR -> navController.navigate(Screen.DoctorPanel.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(
                onLoginClick = {
                    navController.navigate(Screen.Auth.route)
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.PatientPanel.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PatientPanel.route) {
            PatientPanelScreen(
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.DoctorPanel.route) {
            DoctorPanelScreen(
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
