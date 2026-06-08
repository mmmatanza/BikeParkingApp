package es.ubu.bikeparkingapp.data.mapper

import es.ubu.bikeparkingapp.data.dto.ThemeDto
import es.ubu.bikeparkingapp.domain.entity.Theme

fun ThemeDto.toDomain(isUnlocked: Boolean = false, isApplied: Boolean = false) = Theme(
    themeId = themeId,
    name = name,
    cost = cost,
    primaryColor = primaryColor,
    secondaryColor = secondaryColor,
    isUnlocked = isUnlocked,
    isApplied = isApplied
)
