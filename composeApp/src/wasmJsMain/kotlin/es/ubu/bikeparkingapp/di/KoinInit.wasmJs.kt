package es.ubu.bikeparkingapp.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

actual fun initKoin(context: Any?): KoinApplication {
    return startKoin {
        modules(
            appModule,
            supabaseModule,
            viewModelsModule,
            localStorageModule,
            settingsModule
        )
    }
}