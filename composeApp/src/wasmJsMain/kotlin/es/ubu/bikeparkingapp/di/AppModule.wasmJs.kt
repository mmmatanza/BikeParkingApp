package es.ubu.bikeparkingapp.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.dsl.module

actual val settingsModule = module {
    single<Settings> {
        StorageSettings()
    }
}