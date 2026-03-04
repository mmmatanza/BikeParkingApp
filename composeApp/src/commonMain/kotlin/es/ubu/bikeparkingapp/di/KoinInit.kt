package es.ubu.bikeparkingapp.di

import org.koin.core.context.startKoin
import org.koin.core.KoinApplication

// Aquí se incluyen los módulos de Koin para inyección de dependencias
fun initKoin(): KoinApplication {
    return startKoin {
        modules(
            appModule,
            supabaseModule,
            viewModelsModule
        )
    }
}