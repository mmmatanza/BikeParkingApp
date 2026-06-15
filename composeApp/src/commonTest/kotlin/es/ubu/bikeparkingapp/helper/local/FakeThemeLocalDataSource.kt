package es.ubu.bikeparkingapp.helper.local

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import es.ubu.bikeparkingapp.data.local.ThemeLocalDataSource

class FakeThemeLocalDataSource(settings: Settings = MapSettings()) : ThemeLocalDataSource(settings)
