package es.ubu.bikeparkingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThemeDto(
    @SerialName("theme_id") val themeId: String,
    val name: String,
    val cost: Int,
    @SerialName("primary_color") val primaryColor: String,
    @SerialName("secondary_color") val secondaryColor: String
)

@Serializable
data class AccountThemeDto(
    @SerialName("account_id") val accountId: String,
    @SerialName("theme_id") val themeId: String,
    @SerialName("unlocked_at") val unlockedAt: String? = null,
    @SerialName("is_applied") val isApplied: Boolean
)

@Serializable
data class UnlockThemeRequest(
    @SerialName("account_id") val accountId: String,
    @SerialName("theme_id") val themeId: String,
    @SerialName("is_applied") val isApplied: Boolean = false
)
