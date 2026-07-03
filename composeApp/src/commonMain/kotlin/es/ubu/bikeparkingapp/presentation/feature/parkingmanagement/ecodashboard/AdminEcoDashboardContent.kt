package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.eco_dashboard
import bikeparkingapp.composeapp.generated.resources.monthly
import bikeparkingapp.composeapp.generated.resources.no_data_available
import bikeparkingapp.composeapp.generated.resources.top_eco_users
import bikeparkingapp.composeapp.generated.resources.total_distance_saved
import bikeparkingapp.composeapp.generated.resources.weekly
import bikeparkingapp.composeapp.generated.resources.yearly
import es.ubu.bikeparkingapp.domain.entity.UserRanking
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Contenido principal de la pantalla del dashboard de métricas ecológicas.
 * @param state Estado actual de la pantalla.
 * @param onPeriodSelected Acción a realizar cuando se selecciona un nuevo periodo.
 * @param onBackClick Acción a realizar cuando se hace clic en el botón de retroceso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEcoDashboardContent(
    state: AdminEcoDashboardState,
    onPeriodSelected: (EcoPeriod) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.eco_dashboard)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.handCursor()) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Selector de periodo
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                EcoPeriod.entries.forEachIndexed { index, period ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = EcoPeriod.entries.size),
                        onClick = { onPeriodSelected(period) },
                        selected = state.selectedPeriod == period,
                        label = {
                            Text(
                                when (period) {
                                    EcoPeriod.WEEK -> stringResource(Res.string.weekly)
                                    EcoPeriod.MONTH -> stringResource(Res.string.monthly)
                                    EcoPeriod.YEAR -> stringResource(Res.string.yearly)
                                }
                            )
                        }
                    )
                }
            }

            // Distancia Total
            val rawDistance = when (state.selectedPeriod) {
                EcoPeriod.WEEK -> state.metrics?.weeklyDistance
                EcoPeriod.MONTH -> state.metrics?.monthlyDistance
                EcoPeriod.YEAR -> state.metrics?.yearlyDistance
            } ?: 0.0
            val totalDistanceKm = rawDistance / 1000.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.total_distance_saved),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    val formattedDistance = "${(totalDistanceKm * 100).roundToInt() / 100.0}"
                    Text(
                        text = "$formattedDistance km",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Métricas adicionales
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val co2Avoided = totalDistanceKm * 171.00 // g de CO2
                        val pm25Avoided = totalDistanceKm * 0.005 // g de PM2.5
                        val energySaved = totalDistanceKm * 0.60  // kWh

                        MetricLine("CO2 evitado:", "${co2Avoided.roundToInt()} g")
                        MetricLine("Partículas PM2.5 evitadas:", "${(pm25Avoided * 1000).roundToInt()} mg")
                        MetricLine("Ahorro energético:", "${(energySaved * 10).roundToInt() / 10.0} kWh")
                    }
                }
            }

            // Ranking de usuarios
            val topUsers = when (state.selectedPeriod) {
                EcoPeriod.WEEK -> state.metrics?.weeklyTopUsers ?: emptyList()
                EcoPeriod.MONTH -> state.metrics?.monthlyTopUsers ?: emptyList()
                EcoPeriod.YEAR -> state.metrics?.yearlyTopUsers ?: emptyList()
            }

            Text(
                text = stringResource(Res.string.top_eco_users),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            if (topUsers.isEmpty()) {
                Text(
                    text = stringResource(Res.string.no_data_available),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                UserRankingHistogram(topUsers)
            }
        }
    }
}

@Composable
fun MetricLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun UserRankingHistogram(users: List<UserRanking>) {
    val maxDistance = users.maxOfOrNull { it.totalDistance } ?: 1.0

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        users.forEach { user ->
            val userDistanceKm = user.totalDistance / 1000.0
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = user.userName,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${(userDistanceKm * 100).roundToInt() / 100.0} km",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val progress = (user.totalDistance / maxDistance).toFloat()
                val animatedProgress by animateFloatAsState(targetValue = progress)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
    }
}
