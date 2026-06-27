package es.ubu.bikeparkingapp.di

import es.ubu.bikeparkingapp.di.config.configModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

actual fun initKoin(context: Any?): KoinApplication {
    return startKoin {
        modules(
            configModule,
            appModule,
            supabaseModule,
            viewModelsModule,
            localStorageModule,
            settingsModule,
            locationModule
        )
    }
}