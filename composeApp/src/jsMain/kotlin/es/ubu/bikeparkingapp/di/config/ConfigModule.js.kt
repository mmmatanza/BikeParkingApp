package es.ubu.bikeparkingapp.di.config

import es.ubu.bikeparkingapp.config.AppConfig
import org.koin.dsl.module

actual val configModule = module {
    single {
        AppConfig(
            // Dado que no hay BuildConfig se utilizan estos valores
            supabaseUrl = "https://cdnbauyltzxbtxiwipnd.supabase.co",
            supabaseKey = "sb_publishable_kdMhoPCU7e9Y6XmGX2Fxuw_iPLVIe5v",
            analyticsBaseUrl = "http://localhost:8000",
            chatBaseUrl = "http://localhost:8000"
        )
    }
}