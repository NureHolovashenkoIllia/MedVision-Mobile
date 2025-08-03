package ua.nure.holovashenko.medvision_mobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import ua.nure.holovashenko.medvision_mobile.data.local.AuthPreferences
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole
import ua.nure.holovashenko.medvision_mobile.presentation.analysis_detail.AnalysisDetailScreen
import ua.nure.holovashenko.medvision_mobile.presentation.auth.LoginScreen
import ua.nure.holovashenko.medvision_mobile.presentation.auth.RegistrationScreen
import ua.nure.holovashenko.medvision_mobile.presentation.doctor_panel.DoctorPanelScreen
import ua.nure.holovashenko.medvision_mobile.presentation.patient_detail.PatientDetailScreen
import ua.nure.holovashenko.medvision_mobile.presentation.patient_panel.PatientPanelScreen
import ua.nure.holovashenko.medvision_mobile.presentation.profile.ProfileScreen
import ua.nure.holovashenko.medvision_mobile.presentation.upload_analysis.UploadAnalysisScreen

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
                },
                onPatientClick = { patientId ->
                    navController.navigate(Screen.PatientDetail.createRoute(patientId))
                }

            )
        }

        composable(
            route = Screen.PatientDetail.route,
            arguments = listOf(navArgument("patientId") { type = NavType.LongType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val patientId = backStackEntry.arguments?.getLong("patientId") ?: return@composable
            PatientDetailScreen(
                patientId = patientId,
                onBackClick = { navController.popBackStack() },
                onAddAnalysisClick = { pid ->
                    scope.launch {
                        val doctorId = AuthPreferences(context).getDoctorId()
                        if (doctorId != null) {
                            navController.navigate(Screen.UploadAnalysis.createRoute(pid, doctorId))
                        }
                    }
                },
                onAnalysisClick = { analysisId ->
                    navController.navigate(Screen.AnalysisDetail.createRoute(analysisId))
                }
            )
        }

        composable(Screen.AnalysisDetail.route) { backStackEntry ->
            val analysisId = backStackEntry.arguments?.getString("analysisId")?.toLongOrNull()
            if (analysisId != null) {
                AnalysisDetailScreen(
                    analysisId = analysisId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Screen.UploadAnalysis.route,
            arguments = listOf(
                navArgument("patientId") { type = NavType.LongType },
                navArgument("doctorId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getLong("patientId") ?: return@composable
            val doctorId = backStackEntry.arguments?.getLong("doctorId") ?: return@composable
            UploadAnalysisScreen(
                patientId = patientId,
                doctorId = doctorId,
                onAnalysisUploaded = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                    popUpTo(0)
                    }
                           },
                onBack = {
                    navController.popBackStack()
                })
        }
    }
}
