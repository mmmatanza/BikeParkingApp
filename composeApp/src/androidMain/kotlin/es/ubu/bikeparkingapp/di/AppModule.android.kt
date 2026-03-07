package es.ubu.bikeparkingapp.di

import android.content.Context
import org.koin.dsl.module
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual val settingsModule = module {
    single<Settings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences("bike_parking_prefs", Context.MODE_PRIVATE)
        )
    }
}