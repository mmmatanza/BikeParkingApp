package es.ubu.bikeparkingapp.presentation.feature.myimpact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.co2_saved
import bikeparkingapp.composeapp.generated.resources.global_ranking
import bikeparkingapp.composeapp.generated.resources.monthly
import bikeparkingapp.composeapp.generated.resources.my_impact
import bikeparkingapp.composeapp.generated.resources.pm25_saved
import bikeparkingapp.composeapp.generated.resources.ranking_position
import bikeparkingapp.composeapp.generated.resources.total_distance_saved
import bikeparkingapp.composeapp.generated.resources.trees_saved
import bikeparkingapp.composeapp.generated.resources.weekly
import bikeparkingapp.composeapp.generated.resources.yearly
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard.EcoPeriod
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard.MetricLine
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Contenido principal de la pantalla de "Mi Impacto".
 * @param state Estado actual de la pantalla.
 * @param onPeriodSelected Acción a realizar cuando se selecciona un periodo.
 * @param onBackClick Acción a realizar cuando se hace clic en el botón de retroceso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEcoDashboardContent(
    state: UserEcoDashboardState,
    onPeriodSelected: (EcoPeriod) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.my_impact)) },
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

            // Métricas del periodo seleccionado
            val periodMetrics = when (state.selectedPeriod) {
                EcoPeriod.WEEK -> state.metrics?.weeklyMetrics
                EcoPeriod.MONTH -> state.metrics?.monthlyMetrics
                EcoPeriod.YEAR -> state.metrics?.yearlyMetrics
            }

            val totalDistanceKm = (periodMetrics?.distance ?: 0.0) / 1000.0

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
                        // 1 árbol absorbe aprox 20.000g de CO2 al año
                        val treesEquivalent = co2Avoided / 20000.0 

                        MetricLine(stringResource(Res.string.co2_saved) + ":", "${co2Avoided.roundToInt()} g")
                        MetricLine(stringResource(Res.string.pm25_saved) + ":", "${(pm25Avoided * 1000).roundToInt()} mg")
                        MetricLine(stringResource(Res.string.trees_saved) + ":", "${(treesEquivalent * 1000).roundToInt() / 1000.0}")
                    }
                }
            }

            // Ranking
            Text(
                text = stringResource(Res.string.global_ranking),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val position = periodMetrics?.rankingPosition ?: 0
                    val totalUsers = periodMetrics?.totalUsers ?: 0
                    
                    Text(
                        text = stringResource(
                            Res.string.ranking_position,
                            if (position > 0) position.toString() else "-",
                            totalUsers.toString()
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
