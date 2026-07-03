package es.ubu.bikeparkingapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import es.ubu.bikeparkingapp.config.AppConfig
import es.ubu.bikeparkingapp.domain.usecase.auth.HandleDeepLinkUseCase
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginScreen
import es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset.NewPasswordScreen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val handleDeepLinkUseCase: HandleDeepLinkUseCase by inject()
    private val appConfig: AppConfig by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val data = intent?.data
        val isResetLink = data?.host == appConfig.passwordResetHost

        if (isResetLink) {
            // Procesamos el link. El SDK de Supabase extraerá el #access_token automáticamente
            lifecycleScope.launch {
                handleDeepLinkUseCase(data.toString())
            }
        }

        setContent {
            App(initialScreen = if (isResetLink) NewPasswordScreen() else LoginScreen())
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        val data = intent.data
        if (data?.host == appConfig.passwordResetHost) {
            lifecycleScope.launch {
                handleDeepLinkUseCase(data.toString())
            }
            // Forzamos la navegación a la pantalla de nueva contraseña
            setContent {
                App(initialScreen = NewPasswordScreen())
            }
        }
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data
        if (data != null && data.host == appConfig.passwordResetHost) {
            lifecycleScope.launch {
                handleDeepLinkUseCase(data.toString())
            }
        }
    }
}