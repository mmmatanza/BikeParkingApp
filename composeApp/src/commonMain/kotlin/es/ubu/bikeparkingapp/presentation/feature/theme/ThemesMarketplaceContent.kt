package es.ubu.bikeparkingapp.presentation.feature.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.applied
import bikeparkingapp.composeapp.generated.resources.apply
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.cancel
import bikeparkingapp.composeapp.generated.resources.my_points
import bikeparkingapp.composeapp.generated.resources.points
import bikeparkingapp.composeapp.generated.resources.redeem
import bikeparkingapp.composeapp.generated.resources.theme_marketplace
import bikeparkingapp.composeapp.generated.resources.unlock_theme_confirm
import es.ubu.bikeparkingapp.domain.entity.Theme
import org.jetbrains.compose.resources.stringResource

/**
 * Contenido principal de la pantalla del mercado de temas.
 *
 * @param state Estado actual de la pantalla.
 * @param actions Acciones de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemesMarketplaceContent(
    state: ThemesMarketplaceState,
    actions: ThemesMarketplaceActions
) {
    var themeToRedeem by remember { mutableStateOf<Theme?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.theme_marketplace)) },
                navigationIcon = {
                    IconButton(onClick = actions.onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Cabecera con puntos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.my_points),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${state.userPoints} ${stringResource(Res.string.points)}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.theme_marketplace),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.themes) { theme ->
                    ThemeItem(
                        theme = theme,
                        canAfford = state.userPoints >= theme.cost,
                        onRedeemClick = { themeToRedeem = theme },
                        onApplyClick = { actions.onApply(theme.themeId) }
                    )
                }
            }
        }

        // Diálogo de confirmación para canje
        themeToRedeem?.let { theme ->
            AlertDialog(
                onDismissRequest = { themeToRedeem = null },
                title = { Text(stringResource(Res.string.redeem)) },
                text = {
                    Text(
                        stringResource(
                            Res.string.unlock_theme_confirm,
                            theme.cost.toString(),
                            theme.name
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        actions.onRedeem(theme.themeId)
                        themeToRedeem = null
                    }) {
                        Text(stringResource(Res.string.accept))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { themeToRedeem = null }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun ThemeItem(
    theme: Theme,
    canAfford: Boolean,
    onRedeemClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previsualización de colores
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(parseColor(theme.primaryColor)))
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(parseColor(theme.secondaryColor)))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = theme.name, style = MaterialTheme.typography.titleMedium)
                if (!theme.isUnlocked) {
                    Text(
                        text = "${theme.cost} ${stringResource(Res.string.points)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (canAfford) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            if (theme.isApplied) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(stringResource(Res.string.applied), modifier = Modifier.padding(4.dp))
                }
            } else if (theme.isUnlocked) {
                Button(onClick = onApplyClick) {
                    Text(stringResource(Res.string.apply))
                }
            } else {
                Button(
                    onClick = onRedeemClick,
                    enabled = canAfford
                ) {
                    Text(stringResource(Res.string.redeem))
                }
            }
        }
    }
}

/**
 * Función auxiliar para parsear colores en formato Hexadecimal.
 */
private fun parseColor(colorString: String): Long {
    return try {
        val hex = colorString.removePrefix("#")
        val color = if (hex.length == 6) "FF$hex" else hex
        color.toLong(16)
    } catch (e: Exception) {
        0xFF000000 // Negro por defecto en caso de error
    }
}
