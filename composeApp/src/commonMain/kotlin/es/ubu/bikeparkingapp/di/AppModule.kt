package es.ubu.bikeparkingapp.di

import es.ubu.bikeparkingapp.data.local.AccountLocalDataSource
import es.ubu.bikeparkingapp.data.repository.SupabaseAccountRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseAuthRepository
import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
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
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserRoleUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserRoleUseCaseImpl
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginViewModel
import es.ubu.bikeparkingapp.presentation.feature.main.MainViewModel
import es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset.PasswordResetViewModel
import es.ubu.bikeparkingapp.presentation.feature.auth.register.RegisterViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

// En este archivo se definen los módulos que utilizará Koin (injección de dependencias)

// Módulo principal con los repositorio y casos de uso
val appModule = module {

    // Repositorios
    single<AuthRepository> { SupabaseAuthRepository(get()) }
    single<AccountRepository> { SupabaseAccountRepository(get(),get()) }

    // Casos de Uso
    single<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
    single<SignoutUseCase> { SignoutUseCaseImpl(get(), get()) }
    single<GetAuthStateUseCase> { GetAuthStateUseCaseImpl(get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
    single<RequestPasswordResetUseCase> { RequestPasswordResetUseCaseImpl(get()) }
    single<GetUserRoleUseCase> { GetUserRoleUseCaseImpl(get()) }
}

// Módulo para los ViewModels
val viewModelsModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::PasswordResetViewModel)
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

// Módulo para obtener las settings
expect val settingsModule: Module