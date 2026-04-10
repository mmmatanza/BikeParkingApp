package es.ubu.bikeparkingapp

import android.app.Application
import es.ubu.bikeparkingapp.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(applicationContext)
    }
}