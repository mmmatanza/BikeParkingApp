package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingmanagement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.deactivate_parking
import bikeparkingapp.composeapp.generated.resources.deactivate_parking_confirm
import bikeparkingapp.composeapp.generated.resources.toggle_state
import bikeparkingapp.composeapp.generated.resources.toggle_state_confirm
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.entity.isOpen
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ConfirmationDialog
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations.ParkingReservationsScreen
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.publishalert.PublishAlertScreen
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea.UpsertParkingAreaScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de detalle de un parking.
 * @property parkingAreaId Id del parking.
 */
class ParkingManagementScreen(
    val parkingAreaId : String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<ParkingManagementViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(parkingAreaId) {
            viewModel.loadParkingArea(parkingAreaId)
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        // Dar de baja un parking
        ConfirmationDialog(
            isVisible = state.showDeactivateDialog,
            title = stringResource(Res.string.deactivate_parking),
            message = stringResource(Res.string.deactivate_parking_confirm),
            onConfirm = viewModel::onDeactivateConfirm,
            onDismiss = viewModel::onDeactivateDialogDismiss
        )

        // Cambiar estado operativo
        ConfirmationDialog(
            isVisible = state.showToggleDialog,
            title = stringResource(Res.string.toggle_state),
            message = stringResource(Res.string.toggle_state_confirm),
            onConfirm = viewModel::onToggleConfirm,
            onDismiss = viewModel::onToggleServiceDismiss
        )

        // Si se desactiva el parking, se vuelve a la pantalla anterior
        if(state.successDeactivation) navigator.pop()

        println("name: ${state.parking?.name}")
        println("openingTime: ${state.parking?.openingTime}")
        println("closingTime: ${state.parking?.closingTime}")
        println("timezoneId: ${state.parking?.timezoneId}")
        println("openDays: ${state.parking?.openDays}")
        println("isOperative: ${state.parking?.isOperative}")
        println("isOpen: ${state.parking?.isOpen()}")

        ParkingManagementContent(
            state,
            actions = ParkingManagementActions(
                onBackClick = { navigator.pop() },
                onDeactivateClick = viewModel::onDeactivateClick,
                onToggleServiceClick = { viewModel.onToggleServiceClick() },
                onViewOccupancyClick = {parkingId -> navigator.push(ParkingReservationsScreen(parkingId)) },
                onEditDetailsClick = {parkingId -> navigator.push(UpsertParkingAreaScreen(parkingId))},
                onPublishAlertClick = { parkingId -> navigator.push(PublishAlertScreen(parkingId)) },
                onViewEcoDashboardClick = { parkingId -> navigator.push(es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard.AdminEcoDashboardScreen(parkingId)) }
            )
        )
    }
}