package es.ubu.bikeparkingapp.di.config

import es.ubu.bikeparkingapp.BuildConfig
import es.ubu.bikeparkingapp.config.AppConfig
import org.koin.dsl.module

actual val configModule = module {
    single {
        AppConfig(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY,
            analyticsBaseUrl = BuildConfig.ANALYTICS_BASE_URL,
            chatBaseUrl = BuildConfig.CHAT_BASE_URL
        )
    }
}