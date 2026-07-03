package es.ubu.bikeparkingapp.di

import es.ubu.bikeparkingapp.config.AppConfig
import es.ubu.bikeparkingapp.data.local.AccountLocalDataSource
import es.ubu.bikeparkingapp.data.local.ThemeLocalDataSource
import es.ubu.bikeparkingapp.data.repository.AnalyticsOccupancyRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseAccountRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseAlertRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseAuthRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseChatRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseEcoMetricsRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseParkingAreaRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseReservationRepository
import es.ubu.bikeparkingapp.data.repository.SupabaseThemeRepository
import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.AlertRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import es.ubu.bikeparkingapp.domain.repository.ChatRepository
import es.ubu.bikeparkingapp.domain.repository.EcoMetricsRepository
import es.ubu.bikeparkingapp.domain.repository.OccupancyRepository
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository
import es.ubu.bikeparkingapp.domain.repository.ThemeRepository
import es.ubu.bikeparkingapp.domain.usecase.alert.GetAlertsUseCase
import es.ubu.bikeparkingapp.domain.usecase.alert.GetAlertsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAlertAsReadUseCase
import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAlertAsReadUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAllAlertsAsReadUseCase
import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAllAlertsAsReadUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.alert.PublishParkingAlertUseCase
import es.ubu.bikeparkingapp.domain.usecase.alert.PublishParkingAlertUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.HandleDeepLinkUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.HandleDeepLinkUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.RequestPasswordResetUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.RequestPasswordResetUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.SignoutUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.SignoutUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.UpdatePasswordUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.UpdatePasswordUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.chat.GetChatHistoryUseCase
import es.ubu.bikeparkingapp.domain.usecase.chat.GetChatHistoryUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.chat.SendChatMessageUseCase
import es.ubu.bikeparkingapp.domain.usecase.chat.SendChatMessageUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.eco.GetAdminEcoMetricsUseCase
import es.ubu.bikeparkingapp.domain.usecase.eco.GetAdminEcoMetricsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.eco.GetUserEcoMetricsUseCase
import es.ubu.bikeparkingapp.domain.usecase.eco.GetUserEcoMetricsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCase
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.occupancy.GetPredictedOccupancyUseCase
import es.ubu.bikeparkingapp.domain.usecase.occupancy.GetPredictedOccupancyUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.AddParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.AddParkingAreaUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.DeactivateParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.DeactivateParkingAreaUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.GetNearbyParkingAreasUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetNearbyParkingAreasUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreasUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreasUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingDiscoveryUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingDiscoveryUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.ToggleOperativeStateUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.ToggleOperativeStateUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.UpdateParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.UpdateParkingAreaUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.AddReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.AddReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckInReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckInReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckOutReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckOutReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.ExtendReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.ExtendReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetDetailedUserReservationsUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetDetailedUserReservationsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetParkingAreaActiveReservationsUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetParkingAreaActiveReservationsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetUserReservationsUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetUserReservationsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.ReleaseReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.ReleaseReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.theme.ApplyThemeUseCase
import es.ubu.bikeparkingapp.domain.usecase.theme.ApplyThemeUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.theme.GetAppliedThemeUseCase
import es.ubu.bikeparkingapp.domain.usecase.theme.GetAppliedThemeUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.theme.GetThemesMarketplaceUseCase
import es.ubu.bikeparkingapp.domain.usecase.theme.GetThemesMarketplaceUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.theme.RedeemPointsUseCase
import es.ubu.bikeparkingapp.domain.usecase.theme.RedeemPointsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserPointsUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserPointsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserRoleUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserRoleUseCaseImpl
import es.ubu.bikeparkingapp.presentation.feature.alerts.AlertsViewModel
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginViewModel
import es.ubu.bikeparkingapp.presentation.feature.auth.newpassword.NewPasswordViewModel
import es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset.PasswordResetViewModel
import es.ubu.bikeparkingapp.presentation.feature.auth.register.RegisterViewModel
import es.ubu.bikeparkingapp.presentation.feature.chat.ChatViewModel
import es.ubu.bikeparkingapp.presentation.feature.main.MainViewModel
import es.ubu.bikeparkingapp.presentation.feature.myimpact.UserEcoDashboardViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.nearbyparkingareas.NearbyParkingAreasViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.parkingreservation.ParkingReservationViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard.AdminEcoDashboardViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.mapselection.MapSelectionViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.myparkingareas.MyParkingAreasViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingmanagement.ParkingManagementViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations.ParkingReservationsViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.publishalert.PublishAlertViewModel
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea.UpsertParkingAreaViewModel
import es.ubu.bikeparkingapp.presentation.feature.theme.ThemesMarketplaceViewModel
import es.ubu.bikeparkingapp.presentation.feature.trips.mytrips.MyTripsViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// En este archivo se definen los módulos que utilizará Koin (inyección de dependencias)

// Módulo principal con los repositorios y casos de uso
val appModule = module {

    // Repositorios
    single<AuthRepository> { SupabaseAuthRepository(get(), get()) }
    single<AccountRepository> { SupabaseAccountRepository(get(),get()) }
    single<ParkingAreaRepository> { SupabaseParkingAreaRepository(get()) }
    single<ReservationRepository> { SupabaseReservationRepository(get()) }
    single<AlertRepository> { SupabaseAlertRepository(get()) }
    single<OccupancyRepository> { AnalyticsOccupancyRepository(get(), get(), get<AppConfig>().analyticsBaseUrl) }
    single<ThemeRepository> { SupabaseThemeRepository(get(), get()) }
    single<EcoMetricsRepository> { SupabaseEcoMetricsRepository(get()) }
    single<ChatRepository> { SupabaseChatRepository(get(), get(), get<AppConfig>().chatBaseUrl) }

    // HttpClient para analíticas y chat
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
    }


    // Casos de Uso
    // Auth
    single<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
    single<SignoutUseCase> { SignoutUseCaseImpl(get(), get()) }
    single<GetAuthStateUseCase> { GetAuthStateUseCaseImpl(get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
    single<RequestPasswordResetUseCase> { RequestPasswordResetUseCaseImpl(get()) }
    single<UpdatePasswordUseCase> { UpdatePasswordUseCaseImpl(get()) }
    single<HandleDeepLinkUseCase> { HandleDeepLinkUseCaseImpl(get()) }
    single<GetUserRoleUseCase> { GetUserRoleUseCaseImpl(get()) }
    single<GetUserIdUseCase> { GetUserIdUseCaseImpl(get()) }
    // Parking
    single<GetParkingAreasUseCase>{ GetParkingAreasUseCaseImpl(get()) }
    single<AddParkingAreaUseCase>{ AddParkingAreaUseCaseImpl(get()) }
    single<UpdateParkingAreaUseCase>{ UpdateParkingAreaUseCaseImpl(get()) }
    single<DeactivateParkingAreaUseCase>{ DeactivateParkingAreaUseCaseImpl(get()) }
    single<ToggleOperativeStateUseCase>{ ToggleOperativeStateUseCaseImpl(get()) }
    single<GetParkingAreaByIdUseCase>{ GetParkingAreaByIdUseCaseImpl(get()) }
    single<GetNearbyParkingAreasUseCase>{ GetNearbyParkingAreasUseCaseImpl(get()) }
    single<GetParkingDiscoveryUseCase>{ GetParkingDiscoveryUseCaseImpl(get(), get(), get()) }
    single<GetUserLocationUseCase> { GetUserLocationUseCaseImpl(get()) }

    // Reservation
    single<AddReservationUseCase>{ AddReservationUseCaseImpl(get(), get()) }
    single<GetParkingAreaActiveReservationsUseCase>{ GetParkingAreaActiveReservationsUseCaseImpl(get()) }
    single<GetUserReservationsUseCase>{ GetUserReservationsUseCaseImpl(get()) }
    single<CancelReservationUseCase>{ CancelReservationUseCaseImpl(get()) }
    single<CheckInReservationUseCase>{ CheckInReservationUseCaseImpl(get()) }
    single<CheckOutReservationUseCase>{ CheckOutReservationUseCaseImpl(get(), get()) }
    single<ReleaseReservationUseCase>{ ReleaseReservationUseCaseImpl(get()) }
    single<GetDetailedUserReservationsUseCase>{
        GetDetailedUserReservationsUseCaseImpl(
            get(),
            get()
        )
    }
    single<ExtendReservationUseCase>{ ExtendReservationUseCaseImpl(get()) }

    // Alert
    single<GetAlertsUseCase> { GetAlertsUseCaseImpl(get(), get()) }
    single<MarkAlertAsReadUseCase> { MarkAlertAsReadUseCaseImpl(get()) }
    single<MarkAllAlertsAsReadUseCase> { MarkAllAlertsAsReadUseCaseImpl(get(), get()) }
    single<PublishParkingAlertUseCase> { PublishParkingAlertUseCaseImpl(get()) }

    // Occupancy
    single<GetPredictedOccupancyUseCase> { GetPredictedOccupancyUseCaseImpl(get()) }

    // Theme
    single<GetThemesMarketplaceUseCase> { GetThemesMarketplaceUseCaseImpl(get()) }
    single<ApplyThemeUseCase> { ApplyThemeUseCaseImpl(get()) }
    single<RedeemPointsUseCase> { RedeemPointsUseCaseImpl(get(), get()) }
    single<GetUserPointsUseCase> { GetUserPointsUseCaseImpl(get()) }
    single<GetAppliedThemeUseCase> { GetAppliedThemeUseCaseImpl(get()) }

    // Chat
    single<GetChatHistoryUseCase> { GetChatHistoryUseCaseImpl(get()) }
    single<SendChatMessageUseCase> { SendChatMessageUseCaseImpl(get()) }

    // Eco
    single<GetAdminEcoMetricsUseCase> { GetAdminEcoMetricsUseCaseImpl(get()) }
    single<GetUserEcoMetricsUseCase> { GetUserEcoMetricsUseCaseImpl(get(), get()) }
}

// Módulo para los ViewModels
val viewModelsModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::PasswordResetViewModel)
    viewModelOf(::NewPasswordViewModel)
    viewModelOf(::MyParkingAreasViewModel)
    viewModelOf(::UpsertParkingAreaViewModel)
    viewModelOf(::MapSelectionViewModel)
    viewModelOf(::ParkingManagementViewModel)
    viewModelOf(::PublishAlertViewModel)
    viewModelOf(::NearbyParkingAreasViewModel)
    viewModelOf(::ParkingReservationViewModel)
    viewModelOf(::ParkingReservationsViewModel)
    viewModelOf(::MyTripsViewModel)
    viewModelOf(::AlertsViewModel)
    viewModelOf(::ThemesMarketplaceViewModel)
    viewModelOf(::AdminEcoDashboardViewModel)
    viewModelOf(::UserEcoDashboardViewModel)
    viewModelOf(::ChatViewModel)
}

// Módulo para la creación del cliente Supabase
val supabaseModule = module {
    single {
        val config = get<AppConfig>()
        createSupabaseClient(
            supabaseUrl = config.supabaseUrl,
            supabaseKey = config.supabaseKey
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single { get<SupabaseClient>().auth }
    single { get<SupabaseClient>().postgrest }
}

// Módulo para almacenamiento local
val localStorageModule = module {
    single {
        AccountLocalDataSource(get())
    }
    single {
        ThemeLocalDataSource(get())
    }
}

expect val locationModule: Module

// Módulo para obtener las settings
expect val settingsModule: Module