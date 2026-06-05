package es.ubu.bikeparkingapp.data.mapper

import es.ubu.bikeparkingapp.data.dto.ParkingAreaDto
import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import kotlinx.datetime.DayOfWeek

/**
 * Representa la conversión de un objeto [ParkingAreaDto] a un objeto [ParkingArea].
 *
 */
fun ParkingAreaDto.toDomain(): ParkingArea {
    return ParkingArea(
        parkingAreaId = this.parkingAreaId,
        ownerId = this.ownerId,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        capacity = this.capacity,
        currentOccupancy = this.currentOccupancy,
        isOperative = this.isOperative,
        isActive = this.isActive,
        timezoneId = this.timezoneId,
        openingTime = this.openingTime,
        closingTime = this.closingTime,
        openDays = openDays
            .mapNotNull { ordinal -> DayOfWeek.entries.getOrNull(ordinal) }
            .toSet(),
        rules = this.rules,
        occupancyThreshold = this.occupancyThreshold
    )
}

/**
 * Representa la conversión de un objeto [ParkingArea] a un objeto [ParkingAreaDto].
 *
 */
fun ParkingArea.toDto(): ParkingAreaDto {
    return ParkingAreaDto(
        ownerId = this.ownerId,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        capacity = this.capacity,
        currentOccupancy = this.currentOccupancy,
        isOperative = this.isOperative,
        isActive = this.isActive,
        timezoneId = this.timezoneId,
        openingTime = this.openingTime,
        closingTime = this.closingTime,
        openDays = openDays
            .map { it.ordinal }
            .sorted(),
        rules = this.rules,
        occupancyThreshold = this.occupancyThreshold
    )
}