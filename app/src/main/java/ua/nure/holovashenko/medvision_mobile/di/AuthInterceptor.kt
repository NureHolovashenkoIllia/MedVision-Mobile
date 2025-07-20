package ua.nure.holovashenko.medvision_mobile.di

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ua.nure.holovashenko.medvision_mobile.data.local.AuthPreferences
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = runBlocking {
            authPreferences.tokenFlow.firstOrNull()
        }

        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}