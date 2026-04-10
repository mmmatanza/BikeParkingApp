package es.ubu.bikeparkingapp.di

import es.ubu.bikeparkingapp.data.local.AccountLocalDataSource
import es.ubu.bikeparkingapp.data.repository.SupabaseAccountRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseAuthRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseParkingAreaRepository
import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.RequestPasswordResetUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.RequestPasswordResetUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.SignoutUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.SignoutUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.AddParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.AddParkingAreaUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.DeactivateParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.DeactivateParkingAreaUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.GetNearbyParkingAreasUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetNearbyParkingAreasUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreasUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreasUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.ToggleOperativeStateUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.ToggleOperativeStateUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.UpdateParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.UpdateParkingAreaUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserRoleUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserRoleUseCaseImpl
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginViewModel
import es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset.PasswordResetViewModel
import es.ubu.bikeparkingapp.presentation.feature.auth.register.RegisterViewModel
import es.ubu.bikeparkingapp.presentation.feature.main.MainViewModel
import es.ubu.bikeparkingapp.presentation.feature.parking.mapselection.MapSelectionViewModel
import es.ubu.bikeparkingapp.presentation.feature.parking.myparkingareas.MyParkingAreasViewModel
import es.ubu.bikeparkingapp.presentation.feature.parking.nearbyparkingareas.NearbyParkingAreasViewModel
import es.ubu.bikeparkingapp.presentation.feature.parking.parkingmanagement.ParkingManagementViewModel
import es.ubu.bikeparkingapp.presentation.feature.parking.parkingreservation.ParkingReservationViewModel
import es.ubu.bikeparkingapp.presentation.feature.parking.upsertparkingarea.UpsertParkingAreaViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// En este archivo se definen los módulos que utilizará Koin (injección de dependencias)

// Módulo principal con los repositorio y casos de uso
val appModule = module {

    // Repositorios
    single<AuthRepository> { SupabaseAuthRepository(get()) }
    single<AccountRepository> { SupabaseAccountRepository(get(),get()) }
    single<ParkingAreaRepository> { SupabaseParkingAreaRepository(get()) }

    // Casos de Uso
    // Auth
    single<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
    single<SignoutUseCase> { SignoutUseCaseImpl(get(), get()) }
    single<GetAuthStateUseCase> { GetAuthStateUseCaseImpl(get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
    single<RequestPasswordResetUseCase> { RequestPasswordResetUseCaseImpl(get()) }
    single<GetUserRoleUseCase> { GetUserRoleUseCaseImpl(get()) }
    single<GetUserIdUseCase> { GetUserIdUseCaseImpl(get()) }
    // Parking
    single<GetParkingAreasUseCase>{ GetParkingAreasUseCaseImpl(get()) }
    single<AddParkingAreaUseCase>{ AddParkingAreaUseCaseImpl(get()) }
    single<UpdateParkingAreaUseCase>{ UpdateParkingAreaUseCaseImpl(get()) }
    single<DeactivateParkingAreaUseCase>{ DeactivateParkingAreaUseCaseImpl(get()) }
    single<ToggleOperativeStateUseCase>{ ToggleOperativeStateUseCaseImpl(get()) }
    single<GetParkingAreaByIdUseCase>{ GetParkingAreaByIdUseCaseImpl(get()) }
    single<GetNearbyParkingAreasUseCase>{ GetNearbyParkingAreasUseCaseImpl(get()) }
}

// Módulo para los ViewModels
val viewModelsModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::PasswordResetViewModel)
    viewModelOf(::MyParkingAreasViewModel)
    viewModelOf(::UpsertParkingAreaViewModel)
    viewModelOf(::MapSelectionViewModel)
    viewModelOf(::ParkingManagementViewModel)
    viewModelOf(::NearbyParkingAreasViewModel)
    viewModelOf(::ParkingReservationViewModel)
}

// Módulo para la creación del cliente Supabase
val supabaseModule = module {
    // Devolverá la misma instancia de SupabaseClient cada vez que se la necesite
    single {
        // La clave se puede incluir aquí, no es un problema de seguridad dado
        // que se utilizará RLS (seguridad a nivel de fila)
        // otra opción sería utilizar un archivo de propiedades, pero requiere
        // BuildKonfig o utilizar configuraciones expect/actual
        createSupabaseClient(
            supabaseUrl = "https://cdnbauyltzxbtxiwipnd.supabase.co",
            supabaseKey = "sb_publishable_kdMhoPCU7e9Y6XmGX2Fxuw_iPLVIe5v"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single { get<SupabaseClient>().auth }
    single { get<SupabaseClient>().postgrest }
}

// Módulo para almacenamiento local
val localStorageModule = module {
    single {
        AccountLocalDataSource(get())
    }
}

expect val locationModule: Module

// Módulo para obtener las settings
expect val settingsModule: Module