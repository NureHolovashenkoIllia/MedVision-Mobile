package ua.nure.holovashenko.medvision_mobile.presentation.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Registration : Screen("registration")
    object PatientPanel : Screen("patient")

    object DoctorPanel : Screen("doctor")

    object PatientDetail : Screen("patientDetail/{patientId}") {
        fun createRoute(patientId: Long) = "patientDetail/$patientId"
    }

    object AnalysisDetail : Screen("analysisDetail/{analysisId}") {
        fun createRoute(analysisId: Long) = "analysisDetail/$analysisId"
    }

    object UploadAnalysis : Screen("upload-analysis?patientId={patientId}&doctorId={doctorId}") {
        fun createRoute(patientId: Long, doctorId: Long) = "upload-analysis?patientId=$patientId&doctorId=$doctorId"
    }

    object Profile : Screen("profile")
}