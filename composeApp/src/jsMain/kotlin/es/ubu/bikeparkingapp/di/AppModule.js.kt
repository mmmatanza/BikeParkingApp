package es.ubu.bikeparkingapp.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import dev.jordond.compass.geolocation.Geolocator
import es.ubu.bikeparkingapp.data.repository.CompassLocationRepository
import es.ubu.bikeparkingapp.domain.repository.LocationRepository
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCase
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCaseImpl
import org.koin.dsl.module

actual val settingsModule = module {
    single<Settings> {
        StorageSettings()
    }
}
actual val locationModule = module{
    single<Geolocator> { Geolocator() }
    single<LocationRepository> { CompassLocationRepository(get()) }
    single<GetUserLocationUseCase>{ GetUserLocationUseCaseImpl(get()) }
}