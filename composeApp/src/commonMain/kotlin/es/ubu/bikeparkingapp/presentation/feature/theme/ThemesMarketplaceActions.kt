package es.ubu.bikeparkingapp.presentation.feature.theme

/**
 * Acciones que se pueden realizar en la pantalla del mercado de temas.
 *
 * @property onBackClick Volver a la pantalla anterior.
 * @property onRedeem Canjear un tema.
 * @property onApply Aplicar un tema.
 */
data class ThemesMarketplaceActions(
    val onBackClick: () -> Unit,
    val onRedeem: (String) -> Unit,
    val onApply: (String) -> Unit
)
