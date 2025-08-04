package ua.nure.holovashenko.medvision_mobile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.nure.holovashenko.medvision_mobile.data.repository.AnalysisRepositoryImpl
import ua.nure.holovashenko.medvision_mobile.domain.repository.AuthRepository
import ua.nure.holovashenko.medvision_mobile.data.repository.AuthRepositoryImpl
import ua.nure.holovashenko.medvision_mobile.data.repository.DoctorRepositoryImpl
import ua.nure.holovashenko.medvision_mobile.data.repository.PatientRepositoryImpl
import ua.nure.holovashenko.medvision_mobile.data.repository.UserRepositoryImpl
import ua.nure.holovashenko.medvision_mobile.domain.repository.AnalysisRepository
import ua.nure.holovashenko.medvision_mobile.domain.repository.DoctorRepository
import ua.nure.holovashenko.medvision_mobile.domain.repository.PatientRepository
import ua.nure.holovashenko.medvision_mobile.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDoctorRepository(
        impl: DoctorRepositoryImpl
    ): DoctorRepository

    @Binds
    @Singleton
    abstract fun bindAnalysisRepository(
        impl: AnalysisRepositoryImpl
    ): AnalysisRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindPatientRepository(
        impl: PatientRepositoryImpl
    ): PatientRepository
}