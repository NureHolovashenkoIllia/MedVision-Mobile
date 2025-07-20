package ua.nure.holovashenko.medvision_mobile.presentation.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Registration : Screen("registration")
    object PatientPanel : Screen("patient")
    object DoctorPanel : Screen("doctor")
    object Profile : Screen("profile")
}