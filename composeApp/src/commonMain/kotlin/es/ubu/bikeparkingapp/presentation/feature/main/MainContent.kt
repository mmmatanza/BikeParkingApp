package es.ubu.bikeparkingapp.presentation.feature.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.find_parking
import bikeparkingapp.composeapp.generated.resources.my_panel
import bikeparkingapp.composeapp.generated.resources.my_parking_areas_section
import bikeparkingapp.composeapp.generated.resources.my_trips
import bikeparkingapp.composeapp.generated.resources.signout
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido principal de la pantalla principal.
 * @property state Estado actual de la pantalla.
 * @property authState Estado de autenticación.
 * @property actions Acciones de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    state: MainState,
    authState: AuthState,
    actions: MainActions
) {
    if (authState == AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.my_panel), fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Grid de opciones
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pantallas del administrador
                if ((state.userRole != null) && (state.userRole == Role.ADMIN)) {
                    item {
                        MenuOptionCard(
                            title = stringResource(Res.string.my_parking_areas_section),
                            icon = Icons.Default.LocalParking,
                            color = MaterialTheme.colorScheme.errorContainer,
                            onClick = actions.onMyParkingAreas
                        )
                    }
                }

                // Pantallas del usuario
                if ((state.userRole != null) && (state.userRole == Role.USER)) {
                    item {
                        MenuOptionCard(
                            title = stringResource(Res.string.find_parking),
                            icon = Icons.Default.Search,
                            color = MaterialTheme.colorScheme.errorContainer,
                            onClick = actions.onNavigateToNearbyParkingAreas
                        )
                    }
                    item {
                        MenuOptionCard(
                            title = stringResource(Res.string.my_trips),
                            icon = Icons.Default.CardTravel,
                            color = MaterialTheme.colorScheme.errorContainer,
                            onClick = actions.onNavigateToMyTrips
                        )
                    }
                }

                // Botón de cerrar sesión
                item {
                    MenuOptionCard(
                        title = stringResource(Res.string.signout),
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        color = MaterialTheme.colorScheme.errorContainer,
                        onClick = actions.onLogout
                    )
                }
            }
        }
    }
}

@Composable
fun MenuOptionCard(
    title: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .handCursor(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleSmall)
        }
    }
}