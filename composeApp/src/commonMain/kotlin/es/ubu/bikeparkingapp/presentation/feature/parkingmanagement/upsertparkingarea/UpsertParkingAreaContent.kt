package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.add_parking_area
import bikeparkingapp.composeapp.generated.resources.add_parking_rule
import bikeparkingapp.composeapp.generated.resources.address
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.cancel
import bikeparkingapp.composeapp.generated.resources.capacity
import bikeparkingapp.composeapp.generated.resources.closing_time
import bikeparkingapp.composeapp.generated.resources.friday_initial_letter
import bikeparkingapp.composeapp.generated.resources.location_selected
import bikeparkingapp.composeapp.generated.resources.monday_initial_letter
import bikeparkingapp.composeapp.generated.resources.name
import bikeparkingapp.composeapp.generated.resources.open_24_hours
import bikeparkingapp.composeapp.generated.resources.open_days
import bikeparkingapp.composeapp.generated.resources.opening_time
import bikeparkingapp.composeapp.generated.resources.parking_rules
import bikeparkingapp.composeapp.generated.resources.remove_parking_rule
import bikeparkingapp.composeapp.generated.resources.rule_sample
import bikeparkingapp.composeapp.generated.resources.saturday_initial_letter
import bikeparkingapp.composeapp.generated.resources.save_parking_area
import bikeparkingapp.composeapp.generated.resources.select_location
import bikeparkingapp.composeapp.generated.resources.sunday_initial_letter
import bikeparkingapp.composeapp.generated.resources.thursday_initial_letter
import bikeparkingapp.composeapp.generated.resources.tuesday_initial_letter
import bikeparkingapp.composeapp.generated.resources.update_parking_area
import bikeparkingapp.composeapp.generated.resources.wednesday_initial_letter
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de agregar o modificar parking.
 * @property state Estado actual de la pantalla.
 * @property actions Acciones que se pueden realizar en la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpsertParkingAreaContent(
    state: UpsertParkingAreaState,
    actions: UpsertParkingAreaActions
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(if (state.isEditing) Res.string.update_parking_area else Res.string.add_parking_area)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = actions.onBackClick, modifier = Modifier.handCursor()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Nombre del parking
                OutlinedTextField(
                    value = state.name,
                    onValueChange = actions.onNameChange,
                    label = { Text(stringResource(Res.string.name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dirección del parking
                OutlinedTextField(
                    value = state.address,
                    onValueChange = actions.onAddressChange,
                    label = { Text(stringResource(Res.string.address)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Capacidad del parking
                OutlinedTextField(
                    value = if (state.capacity == 0) "" else state.capacity.toString(),
                    onValueChange = { newValue ->
                        // Solo permitimos números y limitamos a una longitud razonable
                        if (newValue.all { it.isDigit() } && newValue.length < 6) {
                            actions.onCapacityChange(newValue.toIntOrNull() ?: 0)
                        }
                    },
                    label = { Text(stringResource(Res.string.capacity)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Horario de apertura
                OutlinedTextField(
                    value = state.openingTime.take(5),
                    onValueChange = { },
                    label = { Text(stringResource(Res.string.opening_time)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (it.isFocused && !state.isOpen24Hours) actions.toggleOpeningPicker(true) },
                    readOnly = true,
                    enabled = !state.isOpen24Hours,
                    trailingIcon = {
                        IconButton(
                            onClick = { actions.toggleOpeningPicker(true) },
                            enabled = !state.isOpen24Hours
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null)
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Horario de Cierre
                OutlinedTextField(
                    value = state.closingTime.take(5),
                    onValueChange = { },
                    label = { Text(stringResource(Res.string.closing_time)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (it.isFocused && !state.isOpen24Hours) actions.toggleClosingPicker(true) },
                    readOnly = true,
                    enabled = !state.isOpen24Hours,
                    trailingIcon = {
                        IconButton(
                            onClick = { actions.toggleClosingPicker(true) },
                            enabled = !state.isOpen24Hours
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null)
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.open_24_hours),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = state.isOpen24Hours,
                        onCheckedChange = actions.onOpen24HoursToggle
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DaySelector(
                    selectedDays = state.openDays,
                    onDayToggle = actions.onDayToggle,
                    modifier = Modifier.fillMaxWidth().handCursor()
                )

                Spacer(modifier = Modifier.height(16.dp))

                ParkingRulesSection(
                    rules = state.rules,
                    currentInput = state.currentRuleInput,
                    onInputChange = actions.onRuleInputChange,
                    onAddRule = actions.onAddRule,
                    onRemoveRule = actions.onRemoveRule
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Selección de ubicación en el mapa
                OutlinedButton(
                    onClick = actions.onNavigateToMap,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.select_location))
                }

                // Muestra una pista si ya hay coordenadas seleccionadas
                if (state.latitude != null) {
                    Text(
                        text = stringResource(Res.string.location_selected),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Muestra también las coordenadas seleccionadas
                    Text(
                        text = "(${state.latitude}, ${state.longitude})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botón de volver
                    OutlinedButton(
                        onClick = actions.onBackClick,
                        modifier = Modifier.weight(1f).handCursor()
                    ) {
                        Text(stringResource(Res.string.back))
                    }

                    // Botón para confirmar
                    Button(
                        onClick = actions.onSaveParkingArea,
                        modifier = Modifier.weight(1f).handCursor(),
                        enabled = actions.validateForm()
                    ) {
                        Text(stringResource(Res.string.save_parking_area))
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingRulesSection(
    rules: List<String>,
    currentInput: String,
    onInputChange: (String) -> Unit,
    onAddRule: () -> Unit,
    onRemoveRule: (Int) -> Unit
) {
    Column {
        Text(stringResource(Res.string.parking_rules), style = MaterialTheme.typography.titleSmall)

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = currentInput,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(Res.string.rule_sample)) },
                singleLine = true
            )
            IconButton(onClick = onAddRule) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.add_parking_rule))
            }
        }

        rules.forEachIndexed { index, rule ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "• $rule",
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onRemoveRule(index) }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(Res.string.remove_parking_rule))
                }
            }
        }
    }
}

@Composable
fun DaySelector(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        DayOfWeek.MONDAY    to stringResource(Res.string.monday_initial_letter),
        DayOfWeek.TUESDAY   to stringResource(Res.string.tuesday_initial_letter),
        DayOfWeek.WEDNESDAY to stringResource(Res.string.wednesday_initial_letter),
        DayOfWeek.THURSDAY  to stringResource(Res.string.thursday_initial_letter),
        DayOfWeek.FRIDAY    to stringResource(Res.string.friday_initial_letter),
        DayOfWeek.SATURDAY  to stringResource(Res.string.saturday_initial_letter),
        DayOfWeek.SUNDAY    to stringResource(Res.string.sunday_initial_letter)
    )

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.open_days),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            days.forEach { (day, label) ->
                val isSelected = day in selectedDays
                val containerColor = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surface
                val contentColor = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant

                Surface(
                    onClick = { onDayToggle(day) },
                    shape = CircleShape,
                    color = containerColor,
                    border = if (!isSelected) BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ) else null,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}