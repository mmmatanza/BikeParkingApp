package es.ubu.bikeparkingapp.di

import com.russhwolf.settings.Settings
import org.koin.dsl.module
import com.russhwolf.settings.StorageSettings

actual val settingsModule = module {
    single<Settings> {
        StorageSettings()
    }
}