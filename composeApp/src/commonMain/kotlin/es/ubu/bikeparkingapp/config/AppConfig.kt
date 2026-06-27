package es.ubu.bikeparkingapp.config

/**
 * Clase de configuración de la aplicación.
 * @property supabaseUrl URL base de Supabase.
 * @property supabaseKey Clave publishable de Supabase.
 * @property analyticsBaseUrl URL base de los servicios de analíticas.
 * @property chatBaseUrl URL base de los servicios de chat.
 */
data class AppConfig(
    val supabaseUrl: String,
    val supabaseKey: String,
    val analyticsBaseUrl: String,
    val chatBaseUrl: String
)