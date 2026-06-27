package es.ubu.bikeparkingapp.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.MobileGeolocator
import es.ubu.bikeparkingapp.data.repository.CompassLocationRepository
import es.ubu.bikeparkingapp.domain.repository.LocationRepository
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCase
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCaseImpl
import org.koin.dsl.module

actual val settingsModule = module {
    single<Settings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences("bike_parking_prefs", Context.MODE_PRIVATE)
        )
    }
}

actual val locationModule = module {
    single<Geolocator> { MobileGeolocator() }
    single<LocationRepository> { CompassLocationRepository(get()) }
    single<GetUserLocationUseCase>{ GetUserLocationUseCaseImpl(get()) }
}
