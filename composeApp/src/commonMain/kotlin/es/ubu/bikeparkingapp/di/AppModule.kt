package es.ubu.bikeparkingapp.di

import es.ubu.bikeparkingapp.presentation.feature.login.LoginViewModel
import es.ubu.bikeparkingapp.presentation.feature.main.MainViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

// En este archivo se definen los módulos que utilizará Koin

// Módulo para los ViewModels
val viewModelsModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
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
            supabaseKey = "b_publishable_kdMhoPCU7e9Y6XmGX2Fxuw_iPLVIe5v"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single { get<SupabaseClient>().auth }
    single { get<SupabaseClient>().postgrest }
}