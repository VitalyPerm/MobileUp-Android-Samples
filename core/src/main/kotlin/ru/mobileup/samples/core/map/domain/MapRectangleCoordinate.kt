package ru.mobileup.samples.core.map.domain

import ru.mobileup.samples.core.location.GeoCoordinate

data class MapRectangleCoordinate(
    val topRight: GeoCoordinate,
    val topLeft: GeoCoordinate,
    val bottomRight: GeoCoordinate,
    val bottomLeft: GeoCoordinate,
    val zoom: Float
)